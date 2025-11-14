package com.example.moviesapp.core.analytics

interface AnalyticsLogger {
    fun logViewMovieDetail(id: Long, title: String)
}
