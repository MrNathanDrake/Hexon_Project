package com.example.cmpt362_project.property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PropertyViewModel : ViewModel() {
//    private val allProperties = mutableListOf(
//        Property(1,
//            "422 Deer View Avenue",
//            "CAD 3550 / month",
//            "https://example.com/image1.jpg",
//            latitude = 49.277748,
//            longitude = -122.909050),
//        Property(2,
//            "2550 Pateseabc Avenue",
//            "CAD 2 / month",
//            "https://example.com/image2.jpg",
//            latitude = 49.267941,
//            longitude = -123.247360)
//    )
//
//    private val _properties = MutableLiveData<List<Property>>()
//    val properties: LiveData<List<Property>> get() = _properties
//
//    init {
//        loadProperties()
//    }
//
//    fun loadProperties() {
//        // 将完整数据加载到 _properties 中
//        _properties.value = allProperties
//    }
//
//    fun deleteProperty(property: Property) {
//        allProperties.remove(property)
//        _properties.value = allProperties
//    }
//
//    fun searchProperties(query: String, filterStatus: String = "All") {
//        val filteredList = allProperties.filter { property ->
//            val matchQuery = property.address.contains(query, ignoreCase = true)
//            val matchStatus = filterStatus == "All" || property.status == filterStatus
//            matchQuery && matchStatus
//        }
//        _properties.value = filteredList
//    }
//
//    fun updatePropertyStatus(property: Property, newStatus: String) {
//        val index = allProperties.indexOfFirst { it.id == property.id }
//        if (index != -1) {
//            allProperties[index] = allProperties[index].copy(status = newStatus)
//            _properties.value = allProperties
//        }
//    }
//
//    fun getAllProperties(): List<Property> = allProperties

    // Firebase Database reference
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("houses")

    // LiveData to observe properties
    private val _properties = MutableLiveData<List<Property>>()
    val properties: LiveData<List<Property>> get() = _properties

    init {
        loadProperties() // Load properties from Firebase on initialization
    }

    fun loadProperties() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val propertyList = mutableListOf<Property>()
                for (propertySnapshot in snapshot.children) {
                    val property = propertySnapshot.getValue(Property::class.java)
                    if (property != null) {
                        // Extract platforms from the snapshot
                        val platformsSnapshot = propertySnapshot.child("platforms")
                        val platforms = platformsSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                        property.platforms = platforms
                        propertyList.add(property)
                    }
                }
                _properties.value = propertyList
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error
                println("Error loading properties: ${error.message}")
            }
        })
    }

    fun deleteProperty(property: Property) {
//        property.id?.let {
//            databaseReference.child(it).removeValue()
//                .addOnSuccessListener {
//                    println("Property deleted successfully.")
//                }
//                .addOnFailureListener { e ->
//                    println("Failed to delete property: ${e.message}")
//                }
//        }

        property.id?.let { propertyId ->
            // Reference to the property in the Realtime Database
            val propertyReference = databaseReference.child(propertyId)

            // Step 1: Retrieve the image URLs from the database before deleting the property
            propertyReference.child("images").get().addOnSuccessListener { snapshot ->
                val imageUrls = snapshot.children.mapNotNull { it.getValue(String::class.java) }

                // Step 2: Delete each image from Firebase Storage
                val storageReference = FirebaseStorage.getInstance().reference
                imageUrls.forEach { imageUrl ->
                    val storagePath = storageReference.storage.getReferenceFromUrl(imageUrl)
                    storagePath.delete()
                        .addOnSuccessListener {
                            println("Image deleted successfully: $imageUrl")
                        }
                        .addOnFailureListener { e ->
                            println("Failed to delete image: ${e.message}")
                        }
                }

                // Step 3: Delete the property data from the Realtime Database
                propertyReference.removeValue()
                    .addOnSuccessListener {
                        println("Property deleted successfully from database.")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to delete property from database: ${e.message}")
                    }
            }.addOnFailureListener { e ->
                println("Failed to retrieve image URLs: ${e.message}")
            }
        }
    }

    fun searchProperties(query: String, filterStatus: String = "All") {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredList = mutableListOf<Property>()
                for (propertySnapshot in snapshot.children) {
                    val property = propertySnapshot.getValue(Property::class.java)
                    if (property != null) {
                        val matchQuery = property.address.contains(query, ignoreCase = true)
                        val matchStatus = filterStatus == "All" || property.status == filterStatus
                        if (matchQuery && matchStatus) {
                            filteredList.add(property)
                        }
                    }
                }
                _properties.value = filteredList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error searching properties: ${error.message}")
            }
        })
    }

    fun updatePropertyStatus(property: Property, newStatus: String) {
        property.id?.let {
            databaseReference.child(it).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    println("Property status updated successfully.")
                }
                .addOnFailureListener { e ->
                    println("Failed to update property status: ${e.message}")
                }
        }
    }


}
