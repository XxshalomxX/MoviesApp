package com.example.moviesapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.moviesapp.BuildConfig
import com.example.moviesapp.data.dto.ApiResponseDto
import com.example.moviesapp.data.dto.MovieDto


interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopular(
        @Query("page") page: Int = 1
    ): ApiResponseDto<MovieDto>
}
