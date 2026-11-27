package com.example.navigationlab.data.location

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null
)