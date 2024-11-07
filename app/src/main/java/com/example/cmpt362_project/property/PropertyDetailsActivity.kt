package com.example.cmpt362_project.property
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cmpt362_project.R

class PropertyDetailsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        val propertyImage: ImageView = findViewById(R.id.propertyImageDetails)
        val propertyAddress: TextView = findViewById(R.id.propertyAddressDetails)
        val propertyPrice: TextView = findViewById(R.id.propertyPriceDetails)

        val propertyAddressText = intent.getStringExtra("property_address") ?: "422 Deer View Avenue"
        val propertyPriceText = intent.getStringExtra("property_price") ?: "3000 CAD / month"
        val propertyImageUrl = intent.getStringExtra("property_image_url") ?: ""

        propertyAddress.text = propertyAddressText
        propertyPrice.text = propertyPriceText
        Glide.with(this)
            .load(propertyImageUrl)
            .error(R.drawable.default_image)
            .into(propertyImage)
    }
}