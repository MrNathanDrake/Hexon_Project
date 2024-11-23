package com.example.cmpt362_project.property

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.cmpt362_project.AddPropertyActivity
import com.example.cmpt362_project.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class PropertyDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        // Back button functionality
        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Closes the activity and goes back
        }

        // Edit button functionality
        val editButton: Button = findViewById(R.id.editButton)
        editButton.setOnClickListener {
            // Navigate to AddPropertyActivity
            val intent = Intent(this, AddPropertyActivity::class.java)
            startActivity(intent)
        }

        // Retrieve property data from the intent
        val propertyAddressText = intent.getStringExtra("property_address") ?: "422 Deer View Avenue"
        val propertyPriceText = intent.getStringExtra("property_price") ?: "3000 CAD / month"
        val propertyImageUrl = intent.getStringExtra("property_image_url") ?: ""

        // Set the property details in the UI
        val propertyAddress: TextView = findViewById(R.id.propertyAddress)
        val propertyPrice: TextView = findViewById(R.id.propertyPrice)
        propertyAddress.text = propertyAddressText
        propertyPrice.text = propertyPriceText

        // Set up the ViewPager for scrolling images (mock example)
        val viewPager: ViewPager2 = findViewById(R.id.propertyImageViewPager)
        val dotsIndicator: DotsIndicator = findViewById(R.id.dotsIndicator)

        // Create a list of image URLs
        val imageUrls = listOf(
            propertyImageUrl,
            "https://example.com/another_image.jpg", // Add additional URLs as needed
            "https://example.com/yet_another_image.jpg"
        )

        // Set up the adapter with the list of image URLs
        viewPager.adapter = PropertyImageAdapter(imageUrls)
        dotsIndicator.setViewPager2(viewPager)
    }
}
