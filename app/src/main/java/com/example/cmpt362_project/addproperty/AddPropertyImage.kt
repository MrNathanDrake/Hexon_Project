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
    private lateinit var mDbRef: DatabaseReference
    private val platforms = mutableSetOf<String>()


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

        binding.checkbox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                platforms.add("Facebook")
            } else {
                platforms.remove("Facebook")
            }
        }
        binding.backButton.setOnClickListener {
            navigateBackToDescription()
        }

        binding.checkbox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                platforms.add("Craiglist")
            } else {
                platforms.remove("Craiglist")
            }
        }


        mDbRef = FirebaseDatabase.getInstance().reference

        val houseId = intent.getStringExtra("houseId") ?: return
        val address = intent.getStringExtra("address") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val province = intent.getStringExtra("province") ?: ""
        val postalCode = intent.getStringExtra("postalCode") ?: ""
        val squareFootage = intent.getStringExtra("squareFootage") ?: ""
        val rent = intent.getStringExtra("rent") ?: ""
        val houseKind = intent.getStringExtra("houseKind") ?: ""
        val bedrooms = intent.getStringExtra("bedrooms") ?: ""
        val baths = intent.getStringExtra("baths") ?: ""
        val description = intent.getStringExtra("description") ?:""
        val status = intent.getStringExtra("status")?: ""
        val features = intent.getSerializableExtra("features") as? Map<String, Boolean> ?: emptyMap()


        binding.submitButton.setOnClickListener {
            if (tempImageUris.values.any { it != null }) {
                val platformsList = platforms.toList()

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
                    "description" to description,
                    "status" to status,
                    "features" to features,
                    "platforms" to platformsList
                )

                uploadAllImagesToFirebase(tempImageUris.filterValues { it != null }, houseId)

                mDbRef.child("houses").child(houseId).setValue(houseData).addOnSuccessListener {
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("platforms", ArrayList(platforms))
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please select at least one image before submitting.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateBackToDescription() {
        val intent = Intent(this, AddPropertyDescription::class.java)
        startActivity(intent)
        finish()
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