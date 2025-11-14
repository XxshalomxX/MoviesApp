package com.example.moviesapp.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.BuildConfig
import com.example.moviesapp.core.AppResult
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.repository.MovieRepository
import com.example.moviesapp.domain.usecase.GetPopularMoviesUseCase
import com.example.moviesapp.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val repo: MovieRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Movie>>> = _state.asStateFlow()

    private val _isLoadingNextPage = MutableStateFlow(false)
    val isLoadingNextPage: StateFlow<Boolean> = _isLoadingNextPage.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()


    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun updateQuery(newValue: String) {
        _query.value = newValue
    }

    private var currentPage = 1
    private var isLoadingMore = false
    private var endReached = false

    init {
        viewModelScope.launch {
            if (BuildConfig.TMDB_API_KEY.isBlank()) {
                _state.value = UiState.Error("Configura TMDB_API_KEY en local.properties")
                return@launch
            }

            val refreshResult = repo.refreshPopular()
            _isOffline.value = refreshResult is AppResult.Failure

            getPopularMovies().collect { result ->
                _state.value = when (result) {
                    is AppResult.Success -> UiState.Success(result.data)
                    is AppResult.Failure -> UiState.Error(result.throwable.message ?: "Error")
                }
            }
        }
    }


    fun onRefresh() {
        viewModelScope.launch {
            val previousState = _state.value
            _state.value = UiState.Loading
            currentPage = 1
            endReached = false
            repo.refreshPopular()
            val res = repo.refreshPopular()
            _isOffline.value = res is AppResult.Failure
            if (res is AppResult.Failure) {
                when (previousState) {
                    is UiState.Success -> _state.value = previousState
                    is UiState.Error   -> _state.value = previousState
                    UiState.Loading    -> {
                        _state.value = UiState.Error("No se pudo actualizar los datos")
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore || endReached) return
        if (_query.value.isNotBlank()) return
        val current = (_state.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            isLoadingMore = true
            _isLoadingNextPage.value = true

            val nextPage = currentPage + 1
            when (val result = getPopularMovies.loadPage(nextPage)) {
                is AppResult.Success -> {
                    _isOffline.value = false
                    val newMovies = result.data
                    if (newMovies.isEmpty()) {
                        endReached = true
                    } else {
                        currentPage = nextPage
                        _state.value = UiState.Success(current + newMovies)
                    }
                }
                is AppResult.Failure -> {
                    _isOffline.value = true
                }
            }

            _isLoadingNextPage.value = false
            isLoadingMore = false
        }
    }
}