package com.example.moviesapp.di

import com.example.moviesapp.data.local.entity.MovieEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder(
            schema = setOf(MovieEntity::class)
        )
            .deleteRealmIfMigrationNeeded() // opcional para desarrollo
            .build()
        return Realm.open(config)
    }
}