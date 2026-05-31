package com.example.movieapp.repository

import android.content.Context
import com.example.movieapp.db.AppDatabase
import com.example.movieapp.db.MovieList
import com.example.movieapp.db.SavedMovie
import com.example.movieapp.model.Movie
import com.example.movieapp.model.GENRE_MAP
import kotlinx.coroutines.flow.Flow

class SavedMovieRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).savedMovieDao()

    fun getMoviesByList(list: MovieList): Flow<List<SavedMovie>> =
        dao.getMoviesByList(list)

    suspend fun getListsForMovie(movieId: Int): Set<MovieList> =
        dao.getListsForMovie(movieId).toSet()

    suspend fun toggleMovie(movie: Movie, list: MovieList) {
        val currentLists = getListsForMovie(movie.id)
        if (list in currentLists) {
            dao.deleteByIdAndList(movie.id, list)
        } else {
            dao.insert(SavedMovie(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                releaseYear = movie.year,
                genreIds = movie.genreIds.joinToString(","),
                voteAverage = movie.voteAverage,
                list = list
            ))
        }
    }

    suspend fun removeFromList(movieId: Int, list: MovieList) {
        dao.deleteByIdAndList(movieId, list)
    }

    suspend fun toggleFromSheet(movie: SavedMovie, targetList: MovieList) {
        val currentLists = getListsForMovie(movie.id)
        if (targetList in currentLists) {
            dao.deleteByIdAndList(movie.id, targetList)
        } else {
            dao.insert(movie.copy(list = targetList))
        }
    }
}

fun SavedMovie.genreNames(): String {
    if (genreIds.isBlank()) return "Brak gatunków"
    return genreIds.split(",")
        .mapNotNull { it.trim().toIntOrNull()?.let { id -> GENRE_MAP[id] } }
        .take(3)
        .joinToString(" • ")
        .ifEmpty { "Brak gatunków" }
}
