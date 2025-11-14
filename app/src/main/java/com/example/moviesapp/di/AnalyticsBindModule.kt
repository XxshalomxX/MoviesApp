package com.example.moviesapp.di

import com.example.moviesapp.core.analytics.AnalyticsLogger
import com.example.moviesapp.core.analytics.FirebaseAnalyticsLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsBindModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsLogger(
        impl: FirebaseAnalyticsLogger
    ): AnalyticsLogger
}


