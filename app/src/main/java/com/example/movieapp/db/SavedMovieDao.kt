package com.example.movieapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedMovieDao {

    @Query("SELECT * FROM saved_movies WHERE `list` = :list ORDER BY title ASC")
    fun getMoviesByList(list: MovieList): Flow<List<SavedMovie>>

    @Query("SELECT `list` FROM saved_movies WHERE id = :movieId")
    suspend fun getListsForMovie(movieId: Int): List<MovieList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: SavedMovie)

    @Query("DELETE FROM saved_movies WHERE id = :movieId AND `list` = :list")
    suspend fun deleteByIdAndList(movieId: Int, list: MovieList)
}
