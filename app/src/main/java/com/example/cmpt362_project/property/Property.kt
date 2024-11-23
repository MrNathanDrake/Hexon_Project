package com.example.cmpt362_project.property

data class Property(
    val id: Int,
    val address: String,
    val price: String,
    val imageUrl: String,
    val latitude: Double?,
    val longitude: Double?,
    var status: String = "Active"
)
