package com.sourcemanager.domain.repository

import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import kotlinx.coroutines.flow.Flow

interface SourceRepository {
    fun getSources(): Flow<List<Source>>
    suspend fun getSourceById(id: String): Source?
    suspend fun addSource(source: Source): Result<Unit>
    suspend fun updateSource(source: Source): Result<Unit>
    suspend fun deleteSource(id: String): Result<Unit>
    suspend fun switchActiveSource(id: String, type: SourceType): Result<Unit>
    suspend fun importFromApi(apiUrl: String): Flow<ImportResult>
    suspend fun importFromJson(jsonContent: String): Flow<ImportResult>
    suspend fun validateSource(source: Source): Result<Unit>
}
