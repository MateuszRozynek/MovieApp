package com.example.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.db.MovieList
import com.example.movieapp.db.SavedMovie
import com.example.movieapp.model.Movie
import com.example.movieapp.repository.SavedMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class SavedViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SavedMovieRepository(app)

    private val _savedStates = MutableStateFlow<Map<Int, Set<MovieList>>>(emptyMap())
    val savedStates: StateFlow<Map<Int, Set<MovieList>>> = _savedStates

    fun getMoviesByList(list: MovieList): Flow<List<SavedMovie>> =
        repo.getMoviesByList(list)

    fun checkSavedStates(movies: List<Movie>) {
        viewModelScope.launch {
            val map = movies.associate { it.id to repo.getListsForMovie(it.id) }
            _savedStates.value = map
        }
    }

    fun toggleMovie(movie: Movie, list: MovieList) {
        viewModelScope.launch {
            repo.toggleMovie(movie, list)
            val newLists = repo.getListsForMovie(movie.id)
            _savedStates.value = _savedStates.value + (movie.id to newLists)
        }
    }

    fun removeFromList(movieId: Int, list: MovieList) {
        viewModelScope.launch {
            repo.removeFromList(movieId, list)
            val newLists = repo.getListsForMovie(movieId)
            _savedStates.value = _savedStates.value + (movieId to newLists)
        }
    }

    fun toggleFromSheet(movie: SavedMovie, targetList: MovieList) {
        viewModelScope.launch {
            repo.toggleFromSheet(movie, targetList)
        }
    }

    suspend fun getListsForMovieSuspend(movieId: Int): Set<MovieList> {
        return withContext(Dispatchers.IO) {
            repo.getListsForMovie(movieId)
        }
    }
}
