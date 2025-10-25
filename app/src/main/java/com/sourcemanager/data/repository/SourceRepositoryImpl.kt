package com.sourcemanager.data.repository

import android.util.Patterns
import com.sourcemanager.data.local.dao.SourceDao
import com.sourcemanager.data.local.entity.toDomain
import com.sourcemanager.data.local.entity.toEntity
import com.sourcemanager.data.remote.api.SourceApiService
import com.sourcemanager.data.remote.dto.toDomain
import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao,
    private val apiService: SourceApiService
) : SourceRepository {
    
    private val json = Json { ignoreUnknownKeys = true }

    override fun getSources(): Flow<List<Source>> {
        return sourceDao.getAllSources().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSourceById(id: String): Source? {
        return sourceDao.getSourceById(id)?.toDomain()
    }

    override suspend fun addSource(source: Source): Result<Unit> {
        return try {
            sourceDao.insertSource(source.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSource(source: Source): Result<Unit> {
        return try {
            val existing = sourceDao.getSourceById(source.id)
            if (existing != null) {
                sourceDao.updateSource(source.toEntity())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Source not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSource(id: String): Result<Unit> {
        return try {
            sourceDao.deleteSourceById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun switchActiveSource(id: String, type: SourceType): Result<Unit> {
        return try {
            sourceDao.setActiveSource(id, type.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importFromApi(apiUrl: String): Flow<ImportResult> = flow {
        try {
            emit(ImportResult.Progress(0, 100))
            delay(500)
            
            val sourceDtos = apiService.importSources(apiUrl)
            
            val sources = sourceDtos.mapIndexed { index, dto ->
                emit(ImportResult.Progress(index + 1, sourceDtos.size))
                delay(100)
                dto.toDomain()
            }
            
            sourceDao.insertSources(sources.map { it.toEntity() })
            emit(ImportResult.Success(sources.size))
        } catch (e: Exception) {
            emit(ImportResult.Error(e.message ?: "Failed to import from API"))
        }
    }

    override suspend fun importFromJson(jsonContent: String): Flow<ImportResult> = flow {
        try {
            emit(ImportResult.Progress(0, 100))
            delay(300)

            val sourceDtos = json.decodeFromString<List<com.sourcemanager.data.remote.dto.SourceDto>>(jsonContent)
            
            val sources = sourceDtos.mapIndexed { index, dto ->
                emit(ImportResult.Progress(index + 1, sourceDtos.size))
                delay(100)
                dto.toDomain()
            }
            
            sourceDao.insertSources(sources.map { it.toEntity() })
            emit(ImportResult.Success(sources.size))
        } catch (e: Exception) {
            emit(ImportResult.Error(e.message ?: "Failed to import from JSON: ${e.localizedMessage}"))
        }
    }

    override suspend fun validateSource(source: Source): Result<Unit> {
        return when {
            source.name.isBlank() -> Result.failure(Exception("Name cannot be empty"))
            source.url.isBlank() -> Result.failure(Exception("URL cannot be empty"))
            !Patterns.WEB_URL.matcher(source.url).matches() -> 
                Result.failure(Exception("Invalid URL format"))
            else -> Result.success(Unit)
        }
    }
}
