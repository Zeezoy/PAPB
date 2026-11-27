package com.example.navigationlab.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.navigationlab.data.repository.FavoriteRepository
import com.example.navigationlab.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: StudentRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadStudent(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)
            try {
                val student = repository.getStudent(id)
                _uiState.value = DetailUiState(student = student)
                observeFavorite(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    errorMessage = e.message ?: "Data tidak tersedia"
                )
            }
        }
    }

    private var favoriteJob: Job? = null

    private fun observeFavorite(id: Int) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoriteRepository.observeIsFavorite(id).collect { value ->
                _uiState.update { it.copy(isFavorite = value) }
            }
        }
    }

    fun toggleFavorite() {
        val student = uiState.value.student ?: return
        viewModelScope.launch {
            if (uiState.value.isFavorite) {
                favoriteRepository.remove(student.id)
            } else {
                favoriteRepository.add(student)
            }
        }
    }

    companion object {
        fun factory(
            repository: StudentRepository,
            favoriteRepository: FavoriteRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetailViewModel(repository, favoriteRepository) }
        }
    }
}