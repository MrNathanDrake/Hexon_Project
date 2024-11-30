package com.example.cmpt362_project.addproperty

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.AddPropertyImageBinding

class AddPropertyImage : AppCompatActivity() {
    private lateinit var binding: AddPropertyImageBinding
    private lateinit var uploadImage: ImageView
    private val REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddPropertyImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.propertyImagetoolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
        binding.propertyImagetoolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.propertyImage1.setOnClickListener {
            uploadImage = binding.propertyImage1
            openGallery()
        }

        binding.propertyImage2.setOnClickListener {
            uploadImage = binding.propertyImage2
            openGallery()
        }


        // Not yet implemented, waiting for connecting to database
//        binding.submitButton.setOnClickListener {
//
//        }


    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri : Uri? = data?.data

            if (imageUri != null) {
                uploadImage.setImageURI(imageUri)
            } else {
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }


}