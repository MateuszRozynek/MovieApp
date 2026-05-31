package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val movies: List<Movie>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    object Empty : SearchUiState()
}

class SearchViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        if (query.length < 2) return

        searchJob = viewModelScope.launch {
            delay(400)
            _uiState.value = SearchUiState.Loading

            repository.searchMovies(query).fold(
                onSuccess = { movies ->
                    _uiState.value = if (movies.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(movies)
                    }
                },
                onFailure = { error ->
                    _uiState.value = SearchUiState.Error(
                        error.message ?: "Nieznany błąd"
                    )
                }
            )
        }
    }
}
