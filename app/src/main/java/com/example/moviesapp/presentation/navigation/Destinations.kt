package com.example.moviesapp.presentation.navigation

object Destinations {
    const val ONBOARDING = "onboarding"
    const val LIST = "list"
    const val DETAIL = "detail/{movieId}"
    fun detail(id: Long) = "detail/$id"
}