package com.example.moviesapp.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseDto<T>(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<T>,
)