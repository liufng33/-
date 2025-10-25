package com.sourcemanager.data.local.dao

import androidx.room.*
import com.sourcemanager.data.local.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    
    @Query("SELECT * FROM sources ORDER BY createdAt DESC")
    fun getAllSources(): Flow<List<SourceEntity>>
    
    @Query("SELECT * FROM sources WHERE id = :id")
    suspend fun getSourceById(id: String): SourceEntity?
    
    @Query("SELECT * FROM sources WHERE type = :type")
    fun getSourcesByType(type: String): Flow<List<SourceEntity>>
    
    @Query("SELECT * FROM sources WHERE type = :type AND isActive = 1")
    suspend fun getActiveSourceByType(type: String): SourceEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SourceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<SourceEntity>)
    
    @Update
    suspend fun updateSource(source: SourceEntity)
    
    @Delete
    suspend fun deleteSource(source: SourceEntity)
    
    @Query("DELETE FROM sources WHERE id = :id")
    suspend fun deleteSourceById(id: String)
    
    @Query("DELETE FROM sources")
    suspend fun deleteAllSources()
    
    @Query("UPDATE sources SET isActive = CASE WHEN id = :id THEN 1 ELSE 0 END WHERE type = :type")
    suspend fun setActiveSource(id: String, type: String)
}
