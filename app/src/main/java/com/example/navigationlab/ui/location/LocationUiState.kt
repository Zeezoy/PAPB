package com.example.navigationlab.ui.location

import com.example.navigationlab.data.location.DeviceLocation

sealed interface LocationUiState {
    data object Idle : LocationUiState
    data object Loading : LocationUiState
    data class Success(
        val location: DeviceLocation,
        val updatedAt: Long
    ) : LocationUiState
    data class Error(val message: String) : LocationUiState
}