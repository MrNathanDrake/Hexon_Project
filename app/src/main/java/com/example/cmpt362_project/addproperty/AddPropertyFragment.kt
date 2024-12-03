package com.example.cmpt362_project.addproperty

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_project.databinding.FragmentAddpropertyBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddPropertyFragment : Fragment() {

    private var _binding: FragmentAddpropertyBinding? = null
    private val binding get() = _binding!!
    private lateinit var mDbRef: DatabaseReference

    private fun showMissingInfoDialog(missingFields: List<String>) {
        val message = "The following fields are missing:\n\n" + missingFields.joinToString("\n") { "- $it" }
        AlertDialog.Builder(requireContext())
            .setTitle("Incomplete Information")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val addPropertyViewModel =
            ViewModelProvider(this).get(AddPropertyViewModel::class.java)

        _binding = FragmentAddpropertyBinding.inflate(inflater, container, false)
        val view = binding.root

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.propertytoolbar)
            supportActionBar?.apply {
                title = ""
                setDisplayHomeAsUpEnabled(true)
            }
        }

        binding.propertytoolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        mDbRef = FirebaseDatabase.getInstance().reference

        binding.availableDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay + 1, selectedMonth, selectedYear)
                binding.availableDateEditText.setText(formattedDate)
            }, year, month, day).show()
        }

        binding.nextStepButton.setOnClickListener {
            val missingFields = mutableListOf<String>()

            val address = binding.propertyEditText.text.toString().takeIf { it.isNotEmpty() } ?: ""
            val city = binding.cityEditText.text.toString().takeIf { it.isNotEmpty() } ?: ""
            val province = binding.provinceSpinner.selectedItem.toString()
            val postalCode = binding.postalCodeEditText.text.toString().takeIf { it.isNotEmpty() } ?: ""
            val postalCodeRegex = Regex("^[A-Za-z]\\d[A-Za-z][ ]?\\d[A-Za-z]\\d$")

            val availableDate = binding.availableDateEditText.text.toString()
            val squareFootage = binding.squareFootageEditText.text.toString()
            val rent = binding.rentEditText.text.toString()
            val houseKind = binding.houseKindSpinner.selectedItem.toString()
            val bedrooms = binding.bedroomsEditText.text.toString()
            val baths = binding.bathsEditText.text.toString()

            if (address.isEmpty()) missingFields.add("Address")
            if (city.isEmpty()) missingFields.add("City")
            if (province == "Select Province") missingFields.add("Province")
            if (availableDate.isEmpty()) missingFields.add("Available Date")
            if (squareFootage.isEmpty()) missingFields.add("Square Footage")
            if (rent.isEmpty()) missingFields.add("Rent")
            if (houseKind == "Select House Kind") missingFields.add("House Kind")
            if (bedrooms.isEmpty()) missingFields.add("Bedrooms")
            if (baths.isEmpty()) missingFields.add("Baths")
            if (postalCode.isEmpty() || !postalCode.matches(postalCodeRegex)) {
                Toast.makeText(requireContext(), "Invalid postal code. Please enter a valid postal code (e.g., A1A 1A1).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (missingFields.isNotEmpty()) {
                showMissingInfoDialog(missingFields)
                return@setOnClickListener
            }

            val hasPet = binding.petEdit.isChecked
            val hasAc = binding.acEdit.isChecked
            val hasFloorHeating = binding.floorEdit.isChecked
            val hasParking = binding.parkingEdit.isChecked
            val hasFurniture = binding.furnitureEdit.isChecked
            val hasEvCharger = binding.evEdit.isChecked

            val houseId = mDbRef.push().key ?: return@setOnClickListener
            val status = "Active"

            val features = mapOf(
                "hasPet" to hasPet,
                "hasAc" to hasAc,
                "hasFloorHeating" to hasFloorHeating,
                "hasParking" to hasParking,
                "hasFurniture" to hasFurniture,
                "hasEvCharger" to hasEvCharger
            )

            val houseData = mapOf(
                "id" to houseId,
                "address" to address,
                "city" to city,
                "province" to province,
                "postalCode" to postalCode,
                "squareFootage" to squareFootage,
                "rent" to rent,
                "houseKind" to houseKind,
                "bedrooms" to bedrooms,
                "baths" to baths,
                "description" to "",
                "status" to status,
                "features" to features
            )

//            mDbRef.child("houses").child(houseId).setValue(houseData).addOnSuccessListener {
                val intent = Intent(requireContext(), AddPropertyDescription::class.java).apply {
                    putExtra("houseId", houseId)
                    putExtra("address", address)
                    putExtra("city", city)
                    putExtra("province", province)
                    putExtra("postalCode", postalCode)
                    putExtra("squareFootage", squareFootage)
                    putExtra("rent", rent)
                    putExtra("houseKind", houseKind)
                    putExtra("bedrooms", bedrooms)
                    putExtra("baths", baths)
                    putExtra("status", status)
                    putExtra("features", HashMap(features))
                }
                startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
