package com.example.navigationlab.ui.detail

import com.example.navigationlab.data.model.Student

data class DetailUiState(
    val isLoading: Boolean = false,
    val student: Student? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)