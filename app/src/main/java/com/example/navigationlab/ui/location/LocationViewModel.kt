package com.example.navigationlab.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.navigationlab.data.location.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    private val _permissionDenied = MutableStateFlow(false)
    val permissionDenied: StateFlow<Boolean> = _permissionDenied.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        _permissionDenied.value = !granted
        if (granted) refreshLocation()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            _permissionDenied.value = false
            _uiState.value = LocationUiState.Loading
            _uiState.value = repository.getCurrentLocation().fold(
                onSuccess = {
                    LocationUiState.Success(it, System.currentTimeMillis())
                },
                onFailure = {
                    LocationUiState.Error(it.message ?: "Gagal mengambil lokasi")
                }
            )
        }
    }

    companion object {
        fun factory(repository: LocationRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { LocationViewModel(repository) }
            }
    }
}