package com.example.navigationlab.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun isGranted(context: android.content.Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionDenied by viewModel.permissionDenied.collectAsStateWithLifecycle()

    var hasFine by remember {
        mutableStateOf(isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION))
    }
    var hasCoarse by remember {
        mutableStateOf(isGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    val hasPermission = hasFine || hasCoarse

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasFine = fine || isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
        hasCoarse = coarse || isGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        viewModel.onPermissionResult(fine || coarse)
    }

    fun requestPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onBack) { Text("Kembali") }

        Text("Jenis akses: " + if (hasFine) "Precise" else if (hasCoarse) "Approximate" else "Belum ada izin")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (hasPermission) viewModel.refreshLocation() else requestPermission()
            }) { Text("Ambil Lokasi Saya") }

            Button(onClick = {
                if (hasPermission) viewModel.refreshLocation() else requestPermission()
            }) { Text("Perbarui Lokasi") }
        }

        if (permissionDenied && !hasPermission) {
            Text("Izin lokasi ditolak. Fitur lokasi tidak dapat digunakan tanpa izin.")
            Button(onClick = { requestPermission() }) { Text("Minta Izin Lagi") }
        }

        when (val state = uiState) {
            LocationUiState.Idle -> Text("Lokasi belum diminta")
            LocationUiState.Loading -> CircularProgressIndicator()
            is LocationUiState.Success -> {
                Text("Latitude: ${state.location.latitude}")
                Text("Longitude: ${state.location.longitude}")
                Text("Accuracy: ${state.location.accuracy ?: 0f} m")
                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(Date(state.updatedAt))
                Text("Terakhir diperbarui: $time")
            }
            is LocationUiState.Error -> {
                Text(state.message)
                Button(onClick = {
                    if (hasPermission) viewModel.refreshLocation() else requestPermission()
                }) { Text("Coba Lagi") }
            }
        }
    }
}