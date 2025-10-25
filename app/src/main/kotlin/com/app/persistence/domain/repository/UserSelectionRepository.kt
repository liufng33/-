package com.app.persistence.domain.repository

import com.app.persistence.domain.model.SelectionType
import com.app.persistence.domain.model.UserSelection
import kotlinx.coroutines.flow.Flow

interface UserSelectionRepository {
    
    fun getAllSelections(): Flow<List<UserSelection>>
    
    suspend fun getSelectionById(id: Long): UserSelection?
    
    fun getSelectionByIdFlow(id: Long): Flow<UserSelection?>
    
    fun getSelectionsBySourceId(sourceId: Long): Flow<List<UserSelection>>
    
    fun getSelectionsByType(itemType: SelectionType): Flow<List<UserSelection>>
    
    suspend fun getSelectionBySourceAndItem(sourceId: Long, itemId: String): UserSelection?
    
    fun getRecentSelectionsByType(itemType: SelectionType, limit: Int = 20): Flow<List<UserSelection>>
    
    suspend fun insertSelection(selection: UserSelection): Long
    
    suspend fun insertSelections(selections: List<UserSelection>): List<Long>
    
    suspend fun updateSelection(selection: UserSelection)
    
    suspend fun deleteSelection(selection: UserSelection)
    
    suspend fun deleteSelectionById(id: Long)
    
    suspend fun deleteSelectionsBySourceId(sourceId: Long)
    
    suspend fun deleteSelectionsByType(itemType: SelectionType)
    
    suspend fun updateLastAccessedTime(id: Long, timestamp: Long = System.currentTimeMillis())
}
