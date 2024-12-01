package com.example.cmpt362_project.property

data class Property(
    val id: String? = null,
    val address: String = "",
    val imageUrl: String = "",
    val city: String = "",
    val province: String = "",
    val postalCode: String = "",
    val squareFootage: String = "",
    val rent: String = "",
    val houseKind: String = "",
    val bedrooms: String = "",
    val baths: String = "",
    val additionalInfo: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: String = "Active"
)
