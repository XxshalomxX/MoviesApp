package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.dto.MovieDto
import com.example.moviesapp.data.local.entity.MovieEntity
import com.example.moviesapp.domain.model.Movie

fun MovieDto.toEntity() = MovieEntity().apply {
    id          = this@toEntity.id
    title       = this@toEntity.title
    overview    = this@toEntity.overview
    posterPath  = this@toEntity.posterPath
    voteAverage = this@toEntity.voteAverage
    releaseDate = this@toEntity.releaseDate
}



fun MovieEntity.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
    voteAverage = voteAverage,
    releaseDate = releaseDate
)