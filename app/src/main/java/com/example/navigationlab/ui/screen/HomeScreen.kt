package com.example.navigationlab.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigationlab.ui.home.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStudentClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onAboutClick: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onProfileClick) { Text("Profile") }
            Button(onClick = onAboutClick) { Text("About") }
            Button(onClick = onRefresh) { Text("Refresh") }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Cari profil") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(uiState.errorMessage)
                    Button(onClick = onRetry) { Text("Coba Lagi") }
                }
            }
            else -> {
                LazyColumn {
                    items(uiState.students) { student ->
                        TextButton(onClick = { onStudentClick(student.id) }) {
                            Text("${student.id} - ${student.name}")
                        }
                    }
                }
            }
        }
    }
}