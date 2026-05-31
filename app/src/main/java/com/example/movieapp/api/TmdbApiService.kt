package com.example.movieapp.api

import com.example.movieapp.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "pl-PL",
        @Query("page") page: Int = 1
    ): SearchResponse
}
