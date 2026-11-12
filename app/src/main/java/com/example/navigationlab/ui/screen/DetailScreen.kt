package com.example.navigationlab.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigationlab.ui.detail.DetailUiState

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Text(uiState.errorMessage)
                Button(onClick = onRetry) { Text("Coba Lagi") }
            }
            else -> {
                uiState.student?.let { data ->
                    Text(data.name, style = MaterialTheme.typography.headlineSmall)
                    Text("Email: ${data.email}")
                    Text("University: ${data.university}")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Kembali") }
    }
}