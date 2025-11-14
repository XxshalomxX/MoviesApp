package com.example.moviesapp.domain.repository

import com.example.moviesapp.core.AppResult
import com.example.moviesapp.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observePopular(): Flow<AppResult<List<Movie>>>
    suspend fun refreshPopular(): AppResult<Unit>
    suspend fun getMovie(id: Long): AppResult<Movie>
    suspend fun getPopularPage(page: Int): AppResult<List<Movie>>
}