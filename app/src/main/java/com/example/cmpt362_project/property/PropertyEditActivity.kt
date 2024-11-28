package com.example.cmpt362_project.property

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cmpt362_project.MainActivity
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.ActivityPropertyEditBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PropertyEditActivity : AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var addressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var provinceEditText: Spinner
    private lateinit var postalEditText: EditText
    private lateinit var squareEditText: EditText
    private lateinit var rentEditText: EditText
    private lateinit var houseEditText: EditText
    private lateinit var bedroomsEditText: EditText
    private lateinit var bathsEditText: EditText

    private lateinit var hasPet: CheckBox
    private lateinit var hasAc: CheckBox
    private lateinit var hasFloorHeating: CheckBox
    private lateinit var hasParking: CheckBox
    private lateinit var hasFurniture: CheckBox
    private lateinit var hasEvCharger: CheckBox

    private lateinit var descriptionEditText: EditText

    private lateinit var databaseReference: DatabaseReference
    private var propertyId: String? = null
    private lateinit var binding: ActivityPropertyEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_edit)

        // 使用 ViewBinding
        binding = ActivityPropertyEditBinding.inflate(layoutInflater)

        // 设置 Toolbar
        setSupportActionBar(binding.propertyEditToolbar)
        supportActionBar?.apply {
            title = "" // 设置工具栏标题为空
            setDisplayHomeAsUpEnabled(true) // 启用左侧返回按钮
        }

        // 设置导航按钮的点击事件
        binding.propertyEditToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // 触发系统返回操作
        }

        // Initialize UI components
        addressEditText = findViewById(R.id.addressEditText)
        cityEditText = findViewById(R.id.newCityEditText)
        provinceEditText = findViewById(R.id.provinceSpinnerEdit)
        postalEditText = findViewById(R.id.newPostalCodeEditText)
        squareEditText = findViewById(R.id.newSquareFootageEditText)
        rentEditText = findViewById(R.id.newRentEditText)
        houseEditText = findViewById(R.id.newHouseKindEditText)
        bedroomsEditText = findViewById(R.id.newBedroomsEditText)
        bathsEditText = findViewById(R.id.newBathsEditText)

        hasPet = findViewById(R.id.newPetEdit)
        hasAc = findViewById(R.id.newAcEdit)
        hasFloorHeating = findViewById(R.id.newFloorEdit)
        hasParking = findViewById(R.id.newParkingEdit)
        hasFurniture = findViewById(R.id.newFurnitureEdit)
        hasEvCharger = findViewById(R.id.newEvEdit)

        descriptionEditText = findViewById(R.id.descriptionEditText)

        saveButton = findViewById(R.id.saveEditButton)

        // Get property ID from the intent
        propertyId = intent.getStringExtra("property_id")

        // Set up Firebase reference
        propertyId?.let { id ->
            databaseReference = FirebaseDatabase.getInstance().getReference("houses/$id")

            // Fetch data and populate fields
            fetchPropertyDetails(id)
        }

        // Save button click listener
        saveButton.setOnClickListener {
            saveUpdatedProperty()
        }
    }

    private fun fetchPropertyDetails(propertyId: String) {
        databaseReference.get().addOnSuccessListener { snapshot ->
            val propertyDetails = snapshot.getValue(PropertyDetails::class.java)

            if (propertyDetails != null) {
                addressEditText.setText(propertyDetails.address)
                cityEditText.setText(propertyDetails.city)
                provinceEditText.setSelection(getProvinceIndex(propertyDetails.province))
                postalEditText.setText(propertyDetails.postalCode)
                squareEditText.setText(propertyDetails.squareFootage)
                rentEditText.setText(propertyDetails.rent)
                houseEditText.setText(propertyDetails.houseKind)
                bedroomsEditText.setText(propertyDetails.bedrooms)
                bathsEditText.setText(propertyDetails.baths)

                hasPet.isChecked = propertyDetails.features.hasPet
                hasAc.isChecked = propertyDetails.features.hasAc
                hasFloorHeating.isChecked = propertyDetails.features.hasFloorHeating
                hasParking.isChecked = propertyDetails.features.hasParking
                hasFurniture.isChecked = propertyDetails.features.hasFurniture
                hasEvCharger.isChecked = propertyDetails.features.hasEvCharger

                descriptionEditText.setText(propertyDetails.description)

            } else {
                Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProvinceIndex(province: String): Int {
        val provinces = resources.getStringArray(R.array.provinces)
        return provinces.indexOf(province).takeIf { it >= 0 } ?: 0
    }


    private fun saveUpdatedProperty() {
        val updatedAddress = addressEditText.text.toString()
        val updatedCity = cityEditText.text.toString()
        val updatedProvince = provinceEditText.selectedItem.toString()
        val updatedPostal = postalEditText.text.toString()
        val updatedSquareFootage = squareEditText.text.toString()
        val updatedRent = rentEditText.text.toString()
        val updatedHouseKind = houseEditText.text.toString()
        val updatedBedrooms = bedroomsEditText.text.toString()
        val updatedBaths = bathsEditText.text.toString()
        val updatedDescription = descriptionEditText.text.toString()

        // Update the 'features' map
        val updatedFeatures = mapOf(
            "hasPet" to hasPet.isChecked,
            "hasAc" to hasAc.isChecked,
            "hasEvCharger" to hasEvCharger.isChecked,
            "hasFloorHeating" to hasFloorHeating.isChecked,
            "hasFurniture" to hasFurniture.isChecked,
            "hasParking" to hasParking.isChecked
        )

        // Create the map for all updated fields
        val updatedProperty = mapOf(
            "address" to updatedAddress,
            "city" to updatedCity,
            "province" to updatedProvince,
            "postalCode" to updatedPostal,
            "squareFootage" to updatedSquareFootage,
            "rent" to updatedRent,
            "houseKind" to updatedHouseKind,
            "bedrooms" to updatedBedrooms,
            "baths" to updatedBaths,
            "description" to updatedDescription,
            "features" to updatedFeatures // Include the features map
        )

        // Update the Firebase database
        databaseReference.updateChildren(updatedProperty).addOnSuccessListener {
            Toast.makeText(this, "Property updated successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to update property: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}