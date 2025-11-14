package com.example.moviesapp.presentation.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.moviesapp.core.oneDecimal
import com.example.moviesapp.core.yyyyMMdd_to_ddMMMYYYY
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.presentation.common.UiState


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoviesListRoute(
    onMovieClick: (Long) -> Unit,
    vm: MoviesListViewModel = hiltViewModel(),
    clearSearchSignal: Boolean = false,
    onClearSearchHandled: () -> Unit = {}
) {
    val state = vm.state.collectAsStateWithLifecycle().value
    val query = vm.query.collectAsStateWithLifecycle().value
    val isLoadingNextPage = vm.isLoadingNextPage.collectAsStateWithLifecycle().value
    val isOffline = vm.isOffline.collectAsStateWithLifecycle().value

    LaunchedEffect(clearSearchSignal) {
        if (clearSearchSignal) {
            vm.updateQuery("")
            onClearSearchHandled()
        }
    }

    MoviesListScreen(
        state = state,
        query = query,
        onQueryChange = vm::updateQuery,
        isLoadingNextPage = isLoadingNextPage,
        isOffline = isOffline,
        onRefresh = vm::onRefresh,
        onMovieClick = onMovieClick,
        onLoadNextPage = vm::loadNextPage
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesListScreen(
    state: UiState<List<Movie>>,
    isLoadingNextPage: Boolean,
    isOffline: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onMovieClick: (Long) -> Unit,
    onLoadNextPage: () -> Unit,
) {

    var isSearching by remember { mutableStateOf(false) }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val behavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(behavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = query,
                            onValueChange = onQueryChange,
                            placeholder = { Text("Buscar película...") },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            trailingIcon = {
                                if (query.isNotBlank()) {
                                    IconButton(onClick = { onQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Limpiar"
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        Text("Películas populares", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { onRefresh() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Actualizar lista"
                        )
                    }
                    IconButton(onClick = {
                        if (isSearching) {
                            onQueryChange("")
                            focusManager.clearFocus()
                        }
                        isSearching = !isSearching
                    }) {
                        Icon(
                            imageVector = if (isSearching) Icons.Rounded.Close else Icons.Rounded.Search,
                            contentDescription = if (isSearching) "Cerrar búsqueda" else "Buscar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.shadow(1.dp),
                scrollBehavior = behavior
            )
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (state) {
                UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Error -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ocurrió un error")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRefresh) { Text("Reintentar") }
                }

                is UiState.Success -> {
                    val movies = state.data
                    val filtered = if (query.isBlank()) movies
                    else movies.filter { it.title.contains(query, ignoreCase = true) }
                    if (isOffline) {
                        Text(
                            text = "Mostrando datos guardados (sin conexión)",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        itemsIndexed(filtered, key = { _, movie -> movie.id }) { index, movie ->
                            MovieItem(movie) { onMovieClick(movie.id) }
                            HorizontalDivider()

                            if (query.isBlank() && index >= filtered.lastIndex - 5) {
                                onLoadNextPage()
                            }
                        }

                        item {
                            if (isLoadingNextPage) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(width = 76.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingChip(movie.voteAverage)
                    Spacer(Modifier.width(12.dp))
                    movie.releaseDate?.let {
                        Text(
                            it.yyyyMMdd_to_ddMMMYYYY(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    movie.overview.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RatingChip(score: Double) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                score.oneDecimal(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
