package com.example.navigationlab.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigationlab.ui.detail.DetailUiState

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
        when {
            uiState.isLoading -> {
                Box(
                    Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
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
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onToggleFavorite) {
                        Text(
                            if (uiState.isFavorite) "Hapus dari Favorit"
                            else "Simpan ke Favorit"
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Kembali") }
    }
}