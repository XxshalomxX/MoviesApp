package com.example.moviesapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.core.analytics.AnalyticsLogger
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.usecase.GetMovieDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.moviesapp.core.AppResult
import com.example.moviesapp.presentation.common.UiState
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getDetail: GetMovieDetailUseCase,
    private val analytics: AnalyticsLogger,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val id: Long = checkNotNull(savedStateHandle["movieId"])
    private val _state = MutableStateFlow<UiState<Movie>>(UiState.Loading)
    val state: StateFlow<UiState<Movie>> = _state


    init {
        viewModelScope.launch {
            when (val result = getDetail(id)) {
                is AppResult.Success -> {
                    _state.value = UiState.Success(result.data)
                    analytics.logViewMovieDetail(result.data.id, result.data.title)
                }
                is AppResult.Failure -> _state.value = UiState.Error(result.throwable.message ?: "Error")
            }
        }
    }
}