package com.example.navigationlab.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigationlab.data.local.FavoriteStudentEntity

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteStudentEntity>,
    sortAscending: Boolean,
    onToggleSort: () -> Unit,
    onStudentClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack) { Text("Kembali") }
            Button(onClick = onToggleSort) {
                Text(if (sortAscending) "Urut: A-Z" else "Urut: Z-A")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (favorites.isEmpty()) {
            Text("Belum ada favorit")
        } else {
            LazyColumn {
                items(favorites) { item ->
                    TextButton(onClick = { onStudentClick(item.id) }) {
                        Column {
                            Text("${item.firstName} ${item.lastName}".trim())
                            Text(item.email, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}