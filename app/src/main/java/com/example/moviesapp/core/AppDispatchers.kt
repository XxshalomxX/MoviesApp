package com.example.moviesapp.core

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class AppDispatchers(
    val io: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)


@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    fun provideDispatchers() = AppDispatchers(
        io = Dispatchers.IO,
        main = Dispatchers.Main,
    )
}