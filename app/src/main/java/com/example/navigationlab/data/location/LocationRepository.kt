package com.example.navigationlab.data.location

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<DeviceLocation>
}