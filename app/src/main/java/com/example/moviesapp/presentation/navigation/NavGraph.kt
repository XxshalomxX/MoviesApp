package com.example.moviesapp.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviesapp.presentation.detail.MovieDetailRoute
import com.example.moviesapp.presentation.list.MoviesListRoute
import com.example.moviesapp.presentation.onboarding.OnboardingRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoviesNavGraph() {
    val navController = rememberNavController()
    var showOnboarding by rememberSaveable { mutableStateOf(true) }

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding) Destinations.ONBOARDING else Destinations.LIST
    ) {

        composable(Destinations.ONBOARDING) {
            OnboardingRoute(
                onContinue = {
                    showOnboarding = false
                    navController.navigate(Destinations.LIST) {
                        popUpTo(Destinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.LIST) {
            val clearSignal by navController.currentBackStackEntry!!
                .savedStateHandle
                .getStateFlow("clearSearch", false)
                .collectAsStateWithLifecycle()
            MoviesListRoute(
                onMovieClick = { id -> navController.navigate(Destinations.detail(id)) },
                clearSearchSignal = clearSignal,
                onClearSearchHandled = {
                    navController.currentBackStackEntry!!
                        .savedStateHandle["clearSearch"] = false
                }

            )
        }
        composable(
            route = Destinations.DETAIL,
            arguments = listOf(navArgument("movieId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("movieId") ?: return@composable
            MovieDetailRoute(
                movieId = id,
                onBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("clearSearch", true)
                    navController.popBackStack()
                }
            )
        }
    }
}