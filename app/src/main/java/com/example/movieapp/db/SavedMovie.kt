package com.example.movieapp.db

import androidx.room.Entity

enum class MovieList {
    WATCHLIST,
    WATCHED,
    FAVOURITE
}

@Entity(tableName = "saved_movies", primaryKeys = ["id", "list"])
data class SavedMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseYear: String,
    val genreIds: String,
    val voteAverage: Double,
    val list: MovieList
)
