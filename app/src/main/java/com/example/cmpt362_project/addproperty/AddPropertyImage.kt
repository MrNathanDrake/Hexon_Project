package com.example.cmpt362_project.addproperty

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmpt362_project.MainActivity
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.AddPropertyImageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddPropertyImage : AppCompatActivity() {
    private lateinit var binding: AddPropertyImageBinding
    private lateinit var uploadImage: ImageView
    private val REQUEST_CODE = 1001
    private val tempImageUris = mutableMapOf<Int, Uri?>()
    private val uploadedImagesUrls = mutableListOf<String>() // List to store successfully uploaded image URLs


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

        setupImageClickListener(binding.propertyImage1, 1)

//        binding.propertyImage2.setOnClickListener {
//            uploadImage = binding.propertyImage2
//            openGallery()
//        }

        val houseId = intent.getStringExtra("houseId") ?: return


        binding.submitButton.setOnClickListener {
            if (tempImageUris.values.any { it != null }) {
                uploadAllImagesToFirebase(tempImageUris.filterValues { it != null }, houseId)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please select at least one image before submitting.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupImageClickListener(imageView: ImageView, imageSlot: Int) {
        imageView.setOnClickListener {
            uploadImage = imageView
            openGallery(imageSlot)
        }
    }

    private fun openGallery(imageSlot: Int) {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_CODE + imageSlot) // Add slot number to distinguish requests
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val slot = requestCode - REQUEST_CODE
            val imageUri: Uri? = data?.data

            if (imageUri != null) {
                tempImageUris[slot] = imageUri // Save the new image URI for this slot
                when (slot) {
                    1 -> binding.propertyImage1.setImageURI(imageUri)
                }
            } else {
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadAllImagesToFirebase(tempImageUris: Map<Int, Uri?>, propertyId: String) {
        val storageReference =
            FirebaseStorage.getInstance().reference.child("property_images/$propertyId")
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("houses/$propertyId/images")

        tempImageUris.forEach { (slot, imageUri) ->
            imageUri?.let {
                val fileName = "slot_${slot}_${UUID.randomUUID()}.jpg"
                val imageRef = storageReference.child(fileName)

                imageRef.putFile(it)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            saveImageUrlToDatabase(uri.toString(), databaseReference)
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Failed to get download URL for slot $slot.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failed to upload image for slot $slot.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }


    private fun saveImageUrlToDatabase(imageUrl: String, databaseReference: DatabaseReference) {
        databaseReference.push().setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Image URL saved to database.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                    Toast.makeText(this, "Failed to save image URL.", Toast.LENGTH_SHORT).show()
            }
        }
}
