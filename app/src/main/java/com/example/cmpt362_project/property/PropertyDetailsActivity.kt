package com.example.cmpt362_project.property

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.cmpt362_project.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class PropertyDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
        val propertyAddressText = intent.getStringExtra("property_address")
        val propertyPriceText = intent.getStringExtra("property_price")
        val propertyImageUrl = intent.getStringExtra("property_image_url")

        // Set the property details in the UI
        val propertyAddress: TextView = findViewById(R.id.propertyAddress)
        val propertyPrice: TextView = findViewById(R.id.propertyPrice)
        propertyAddress.text = propertyAddressText
        propertyPrice.text = propertyPriceText

        // Set up the ViewPager for scrolling images
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
