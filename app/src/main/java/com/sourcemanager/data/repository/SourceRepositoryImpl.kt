package com.sourcemanager.data.repository

import android.util.Patterns
import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SourceRepositoryImpl : SourceRepository {
    private val _sources = MutableStateFlow<List<Source>>(emptyList())
    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    data class SourceDto(
        val id: String,
        val name: String,
        val type: String,
        val url: String,
        val isActive: Boolean = false,
        val description: String = ""
    )

    override fun getSources(): Flow<List<Source>> = _sources.asStateFlow()

    override suspend fun getSourceById(id: String): Source? {
        return _sources.value.find { it.id == id }
    }

    override suspend fun addSource(source: Source): Result<Unit> {
        return try {
            val currentSources = _sources.value.toMutableList()
            currentSources.add(source)
            _sources.value = currentSources
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSource(source: Source): Result<Unit> {
        return try {
            val currentSources = _sources.value.toMutableList()
            val index = currentSources.indexOfFirst { it.id == source.id }
            if (index != -1) {
                currentSources[index] = source
                _sources.value = currentSources
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
            val currentSources = _sources.value.toMutableList()
            currentSources.removeAll { it.id == id }
            _sources.value = currentSources
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun switchActiveSource(id: String, type: SourceType): Result<Unit> {
        return try {
            val currentSources = _sources.value.toMutableList()
            val updatedSources = currentSources.map { source ->
                if (source.type == type) {
                    source.copy(isActive = source.id == id)
                } else {
                    source
                }
            }
            _sources.value = updatedSources
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importFromApi(apiUrl: String): Flow<ImportResult> = flow {
        try {
            emit(ImportResult.Progress(0, 100))
            delay(500)
            
            val sampleSources = listOf(
                SourceDto(
                    id = "api-1",
                    name = "API Source 1",
                    type = "SEARCH",
                    url = "https://api.example.com/search",
                    description = "Imported from API"
                ),
                SourceDto(
                    id = "api-2",
                    name = "API Source 2",
                    type = "PARSER",
                    url = "https://api.example.com/parser",
                    description = "Imported from API"
                )
            )

            val currentSources = _sources.value.toMutableList()
            sampleSources.forEachIndexed { index, dto ->
                val source = Source(
                    id = dto.id,
                    name = dto.name,
                    type = SourceType.valueOf(dto.type),
                    url = dto.url,
                    isActive = dto.isActive,
                    description = dto.description
                )
                currentSources.add(source)
                emit(ImportResult.Progress(index + 1, sampleSources.size))
                delay(200)
            }

            _sources.value = currentSources
            emit(ImportResult.Success(sampleSources.size))
        } catch (e: Exception) {
            emit(ImportResult.Error(e.message ?: "Failed to import from API"))
        }
    }

    override suspend fun importFromJson(jsonContent: String): Flow<ImportResult> = flow {
        try {
            emit(ImportResult.Progress(0, 100))
            delay(300)

            val sources = json.decodeFromString<List<SourceDto>>(jsonContent)
            val currentSources = _sources.value.toMutableList()

            sources.forEachIndexed { index, dto ->
                val source = Source(
                    id = dto.id,
                    name = dto.name,
                    type = SourceType.valueOf(dto.type),
                    url = dto.url,
                    isActive = dto.isActive,
                    description = dto.description
                )
                currentSources.add(source)
                emit(ImportResult.Progress(index + 1, sources.size))
                delay(100)
            }

            _sources.value = currentSources
            emit(ImportResult.Success(sources.size))
        } catch (e: Exception) {
            emit(ImportResult.Error(e.message ?: "Failed to import from JSON"))
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
