package com.example.cmpt362_project.property


data class PropertyDetails(
    val id: String? = null,
    val address: String = "",
    val rent: String = "",
    val houseKind: String = "",
    val bedrooms: String = "",
    val baths: String = "",
    val squareFootage: String = "",
    val features: Features = Features(),
    val description: String = "",

    val city: String = "",
    val province: String = "",
    val postalCode: String = "",
    val imageUrls: List<String>? = null,
    val imageUrl: String = ""
)

data class Features(
    val hasAc: Boolean = false,
    val hasEvCharger: Boolean = false,
    val hasFloorHeating: Boolean = false,
    val hasFurniture: Boolean = false,
    val hasParking: Boolean = false,
    val hasPet: Boolean = false,
)