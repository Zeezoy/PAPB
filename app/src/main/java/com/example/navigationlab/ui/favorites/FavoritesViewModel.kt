package com.example.navigationlab.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.navigationlab.data.local.FavoriteStudentEntity
import com.example.navigationlab.data.preferences.UserPreferencesRepository
import com.example.navigationlab.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    favoriteRepository: FavoriteRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val sortAscending: StateFlow<Boolean> = preferencesRepository.sortAscending
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val favorites: StateFlow<List<FavoriteStudentEntity>> = combine(
        favoriteRepository.observeFavorites(),
        preferencesRepository.sortAscending
    ) { list, ascending ->
        if (ascending) list.sortedBy { it.firstName }
        else list.sortedByDescending { it.firstName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSort() {
        viewModelScope.launch {
            preferencesRepository.saveSortAscending(!sortAscending.value)
        }
    }

    companion object {
        fun factory(
            favoriteRepository: FavoriteRepository,
            preferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { FavoritesViewModel(favoriteRepository, preferencesRepository) }
        }
    }
}