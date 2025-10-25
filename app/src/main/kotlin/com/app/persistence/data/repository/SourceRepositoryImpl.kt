package com.app.persistence.data.repository

import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.local.database.entity.SourceEntity
import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.SourceType
import com.app.persistence.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SourceRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao
) : SourceRepository {
    
    override fun getAllSources(): Flow<List<Source>> {
        return sourceDao.getAllSources().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getSourceById(id: Long): Source? {
        return sourceDao.getSourceById(id)?.toDomain()
    }
    
    override fun getSourceByIdFlow(id: Long): Flow<Source?> {
        return sourceDao.getSourceByIdFlow(id).map { it?.toDomain() }
    }
    
    override suspend fun getSourceByName(name: String): Source? {
        return sourceDao.getSourceByName(name)?.toDomain()
    }
    
    override fun getSourcesByType(type: SourceType): Flow<List<Source>> {
        return sourceDao.getSourcesByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getEnabledSources(): Flow<List<Source>> {
        return sourceDao.getEnabledSources().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun insertSource(source: Source): Long {
        return sourceDao.insertSource(SourceEntity.fromDomain(source))
    }
    
    override suspend fun insertSources(sources: List<Source>): List<Long> {
        return sourceDao.insertSources(sources.map { SourceEntity.fromDomain(it) })
    }
    
    override suspend fun updateSource(source: Source) {
        sourceDao.updateSource(SourceEntity.fromDomain(source))
    }
    
    override suspend fun deleteSource(source: Source) {
        sourceDao.deleteSource(SourceEntity.fromDomain(source))
    }
    
    override suspend fun deleteSourceById(id: Long) {
        sourceDao.deleteSourceById(id)
    }
    
    override suspend fun updateSourceEnabled(id: Long, isEnabled: Boolean) {
        sourceDao.updateSourceEnabled(id, isEnabled)
    }
    
    override suspend fun getSourceCount(): Int {
        return sourceDao.getSourceCount()
    }
}
