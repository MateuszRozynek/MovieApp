package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val overview: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int>
) {
    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

    val year: String
        get() = if (!releaseDate.isNullOrBlank()) releaseDate.take(4) else "?"
}

data class SearchResponse(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)

val GENRE_MAP = mapOf(
    28 to "Akcja",
    12 to "Przygodowy",
    16 to "Animowany",
    35 to "Komedia",
    80 to "Kryminał",
    99 to "Dokumentalny",
    18 to "Dramat",
    10751 to "Familijny",
    14 to "Fantasy",
    36 to "Historyczny",
    27 to "Horror",
    10402 to "Muzyczny",
    9648 to "Tajemnica",
    10749 to "Romans",
    878 to "Sci-Fi",
    10770 to "TV Movie",
    53 to "Thriller",
    10752 to "Wojenny",
    37 to "Western"
)
