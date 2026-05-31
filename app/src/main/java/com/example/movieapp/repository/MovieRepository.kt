package com.example.movieapp.repository

import com.example.movieapp.api.RetrofitClient
import com.example.movieapp.model.Movie

class MovieRepository {

    private val api = RetrofitClient.apiService
    private val apiKey = "f61b221b9652588caa5fb69d3391f782"

    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            val response = api.searchMovies(apiKey = apiKey, query = query)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
