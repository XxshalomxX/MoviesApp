package com.example.moviesapp.data.local

import com.example.moviesapp.data.local.entity.MovieEntity
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieDao @Inject constructor(private val realm: io.realm.kotlin.Realm) {
    fun observeAll(): Flow<List<MovieEntity>> = realm.query<MovieEntity>().asFlow()
        .map { it.list.toList() }


    suspend fun replaceAll(movies: List<MovieEntity>) {
        realm.write {
            delete(query<MovieEntity>())
            movies.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    suspend fun insertAll(movies: List<MovieEntity>) {
        realm.write {
            movies.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    suspend fun findById(id: Long): MovieEntity? =
        realm.query<MovieEntity>("id == $0", id)
        .first()
        .find()
}

