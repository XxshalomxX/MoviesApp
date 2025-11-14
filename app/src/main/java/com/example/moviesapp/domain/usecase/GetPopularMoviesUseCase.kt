package com.example.moviesapp.domain.usecase

import com.example.moviesapp.core.AppResult
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(private val repo: MovieRepository) {
    operator fun invoke(): Flow<AppResult<List<Movie>>> = repo.observePopular()
    suspend fun loadPage(page: Int): AppResult<List<Movie>> =
        repo.getPopularPage(page)
}