package com.app.persistence.domain.repository

import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.SourceType
import kotlinx.coroutines.flow.Flow

interface SourceRepository {
    
    fun getAllSources(): Flow<List<Source>>
    
    suspend fun getSourceById(id: Long): Source?
    
    fun getSourceByIdFlow(id: Long): Flow<Source?>
    
    suspend fun getSourceByName(name: String): Source?
    
    fun getSourcesByType(type: SourceType): Flow<List<Source>>
    
    fun getEnabledSources(): Flow<List<Source>>
    
    suspend fun insertSource(source: Source): Long
    
    suspend fun insertSources(sources: List<Source>): List<Long>
    
    suspend fun updateSource(source: Source)
    
    suspend fun deleteSource(source: Source)
    
    suspend fun deleteSourceById(id: Long)
    
    suspend fun updateSourceEnabled(id: Long, isEnabled: Boolean)
    
    suspend fun getSourceCount(): Int
}
