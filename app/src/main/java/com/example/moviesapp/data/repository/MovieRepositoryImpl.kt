package com.example.moviesapp.data.repository

import com.example.moviesapp.core.AppDispatchers
import com.example.moviesapp.core.AppResult
import com.example.moviesapp.data.local.MovieDao
import com.example.moviesapp.data.mapper.toDomain
import com.example.moviesapp.data.mapper.toEntity
import com.example.moviesapp.data.remote.TmdbApi
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val dao: MovieDao,
    private val dispatchers: AppDispatchers,
) : MovieRepository {

    override fun observePopular(): Flow<AppResult<List<Movie>>> =
        dao.observeAll()
            .map { list -> AppResult.Success(list.map { it.toDomain() }) as AppResult<List<Movie>> }
            .catch { emit(AppResult.Failure(it)) }

    override suspend fun refreshPopular(): AppResult<Unit> = withContext(dispatchers.io) {
        return@withContext try {
            val response = api.getPopular()
            val entities = response.results.map { it.toEntity() }
            dao.replaceAll(entities)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t)
        }
    }

    override suspend fun getMovie(id: Long): AppResult<Movie> = withContext(dispatchers.io) {
        val entity = dao.findById(id)
            ?: return@withContext AppResult.Failure(NoSuchElementException("Not found"))
        AppResult.Success(entity.toDomain())
    }

    override suspend fun getPopularPage(page: Int): AppResult<List<Movie>> =
        withContext(dispatchers.io) {
            return@withContext try {
                val response = api.getPopular(page = page)
                val entities = response.results.map { it.toEntity() }
                dao.insertAll(entities)
                AppResult.Success(entities.map { it.toDomain() })
            } catch (t: Throwable) {
                AppResult.Failure(t)
            }
        }
}