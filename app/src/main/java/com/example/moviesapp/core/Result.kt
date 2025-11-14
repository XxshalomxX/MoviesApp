package com.example.moviesapp.core

sealed class AppResult<out T> {
    data class Success<T>(val data: T): AppResult<T>()
    data class Failure(val throwable: Throwable): AppResult<Nothing>()
}