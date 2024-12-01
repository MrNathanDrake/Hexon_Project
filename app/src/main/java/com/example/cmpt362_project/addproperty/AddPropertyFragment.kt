package com.example.cmpt362_project.addproperty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_project.databinding.FragmentAddpropertyBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddPropertyFragment : Fragment() {

    private var _binding: FragmentAddpropertyBinding? = null
    private val binding get() = _binding!!
    private lateinit var mDbRef: DatabaseReference

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

        binding.nextStepButton.setOnClickListener {
            val address = binding.propertyEditText.text.toString().takeIf { it.isNotEmpty() } ?: "8888 University Dr W"
            val city = binding.cityEditText.text.toString().takeIf { it.isNotEmpty() } ?: "Burnaby"
            val province = binding.provinceSpinner.selectedItem.toString()
            val postalCode = binding.postalCodeEditText.text.toString().takeIf { it.isNotEmpty() } ?: "V3T 0K6"

            val squareFootage = binding.squareFootageEditText.text.toString().takeIf { it.isNotEmpty() } ?: "0"
            val rent = binding.rentEditText.text.toString().takeIf { it.isNotEmpty() } ?: "0"
            val houseKind = binding.houseKindEditText.text.toString().takeIf { it.isNotEmpty() } ?: "House"
            val bedrooms = binding.bedroomsEditText.text.toString().takeIf { it.isNotEmpty() } ?: "0"
            val baths = binding.bathsEditText.text.toString().takeIf { it.isNotEmpty() } ?: "0"

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
//            }.addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
//            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
