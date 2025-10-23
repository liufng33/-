package com.app.persistence.data.repository

import com.app.persistence.data.local.database.dao.UserSelectionDao
import com.app.persistence.data.local.database.entity.UserSelectionEntity
import com.app.persistence.domain.model.SelectionType
import com.app.persistence.domain.model.UserSelection
import com.app.persistence.domain.repository.UserSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSelectionRepositoryImpl @Inject constructor(
    private val userSelectionDao: UserSelectionDao
) : UserSelectionRepository {
    
    override fun getAllSelections(): Flow<List<UserSelection>> {
        return userSelectionDao.getAllSelections().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getSelectionById(id: Long): UserSelection? {
        return userSelectionDao.getSelectionById(id)?.toDomain()
    }
    
    override fun getSelectionByIdFlow(id: Long): Flow<UserSelection?> {
        return userSelectionDao.getSelectionByIdFlow(id).map { it?.toDomain() }
    }
    
    override fun getSelectionsBySourceId(sourceId: Long): Flow<List<UserSelection>> {
        return userSelectionDao.getSelectionsBySourceId(sourceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getSelectionsByType(itemType: SelectionType): Flow<List<UserSelection>> {
        return userSelectionDao.getSelectionsByType(itemType.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getSelectionBySourceAndItem(sourceId: Long, itemId: String): UserSelection? {
        return userSelectionDao.getSelectionBySourceAndItem(sourceId, itemId)?.toDomain()
    }
    
    override fun getRecentSelectionsByType(itemType: SelectionType, limit: Int): Flow<List<UserSelection>> {
        return userSelectionDao.getRecentSelectionsByType(itemType.name, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun insertSelection(selection: UserSelection): Long {
        return userSelectionDao.insertSelection(UserSelectionEntity.fromDomain(selection))
    }
    
    override suspend fun insertSelections(selections: List<UserSelection>): List<Long> {
        return userSelectionDao.insertSelections(selections.map { UserSelectionEntity.fromDomain(it) })
    }
    
    override suspend fun updateSelection(selection: UserSelection) {
        userSelectionDao.updateSelection(UserSelectionEntity.fromDomain(selection))
    }
    
    override suspend fun deleteSelection(selection: UserSelection) {
        userSelectionDao.deleteSelection(UserSelectionEntity.fromDomain(selection))
    }
    
    override suspend fun deleteSelectionById(id: Long) {
        userSelectionDao.deleteSelectionById(id)
    }
    
    override suspend fun deleteSelectionsBySourceId(sourceId: Long) {
        userSelectionDao.deleteSelectionsBySourceId(sourceId)
    }
    
    override suspend fun deleteSelectionsByType(itemType: SelectionType) {
        userSelectionDao.deleteSelectionsByType(itemType.name)
    }
    
    override suspend fun updateLastAccessedTime(id: Long, timestamp: Long) {
        userSelectionDao.updateLastAccessedTime(id, timestamp)
    }
}
