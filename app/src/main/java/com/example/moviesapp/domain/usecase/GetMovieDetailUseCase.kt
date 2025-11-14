package com.example.moviesapp.domain.usecase

import com.example.moviesapp.core.AppResult
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val repo: MovieRepository) {
    suspend operator fun invoke(id: Long): AppResult<Movie> = repo.getMovie(id)
}