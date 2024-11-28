package com.example.cmpt362_project.property

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.cmpt362_project.R
import com.google.firebase.database.FirebaseDatabase
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class PropertyDetailsActivity : AppCompatActivity() {

    private lateinit var editButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        editButton = findViewById(R.id.editButton)

        // Enable the default back button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Show back button
            title = "" // Optional: Clear default title
        }

        // Set the back button's behavior
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button action
        }

        // Retrieve property data from the intent
        val propertyId =
            intent.getStringExtra("property_id") // Property ID for fetching Firebase data

        // Fetch property details from Firebase
        if (propertyId != null) {
            fetchPropertyDetails(propertyId)
        }

        editButton.setOnClickListener {
            val intent = Intent(this, PropertyEditActivity::class.java).apply {
                putExtra("property_id", propertyId)
                // Pass additional property details if necessary
            }
            startActivity(intent)
        }
    }
    private fun fetchPropertyDetails(propertyId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("houses/$propertyId")
        databaseReference.get().addOnSuccessListener { snapshot ->
            val propertyDetails = snapshot.getValue(PropertyDetails::class.java)
            if (propertyDetails != null) {
                populateUI(propertyDetails)
            } else {
                Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateUI(details: PropertyDetails) {
        // Property details
        findViewById<TextView>(R.id.propertyAddress).text = details.address
        findViewById<TextView>(R.id.propertyPrice).text = "CAD ${details.rent} / month"
        findViewById<TextView>(R.id.propertyType).text = details.houseKind

        // Basic info
        findViewById<TextView>(R.id.bedroomsTextView).text = "${details.bedrooms} Beds"
        findViewById<TextView>(R.id.bathroomsTextView).text = "${details.baths} Baths"
        findViewById<TextView>(R.id.squareFootageTextView).text = "${details.squareFootage} ft²"

        findViewById<TextView>(R.id.propertyDescription).text = details.description

        // Populate amenities
        populateAmenities(details.features)


        // Image ViewPager
        val viewPager: ViewPager2 = findViewById(R.id.propertyImageViewPager)
        val dotsIndicator: DotsIndicator = findViewById(R.id.dotsIndicator)
        viewPager.adapter = PropertyImageAdapter(details.imageUrls ?: emptyList())
        dotsIndicator.setViewPager2(viewPager)
    }


    private fun populateAmenities(features: Features) {
        findViewById<ImageView>(R.id.petsImageView).setImageResource(
            if (features.hasPet) R.drawable.yes else R.drawable.no
        )
        findViewById<ImageView>(R.id.airConditionImageView).setImageResource(
            if (features.hasAc) R.drawable.yes else R.drawable.no
        )
        findViewById<ImageView>(R.id.parkingImageView).setImageResource(
            if (features.hasParking) R.drawable.yes else R.drawable.no
        )
        findViewById<ImageView>(R.id.evChargerImageView).setImageResource(
            if (features.hasEvCharger) R.drawable.yes else R.drawable.no
        )
        findViewById<ImageView>(R.id.furnishedImageView).setImageResource(
            if (features.hasFurniture) R.drawable.yes else R.drawable.no
        )
        findViewById<ImageView>(R.id.heatingImageView).setImageResource(
            if (features.hasFloorHeating) R.drawable.yes else R.drawable.no
        )
    }


}
