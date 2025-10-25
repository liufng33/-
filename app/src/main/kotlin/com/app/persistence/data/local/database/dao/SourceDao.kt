package com.app.persistence.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.persistence.data.local.database.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    
    @Query("SELECT * FROM sources ORDER BY priority DESC, name ASC")
    fun getAllSources(): Flow<List<SourceEntity>>
    
    @Query("SELECT * FROM sources WHERE id = :id")
    suspend fun getSourceById(id: Long): SourceEntity?
    
    @Query("SELECT * FROM sources WHERE id = :id")
    fun getSourceByIdFlow(id: Long): Flow<SourceEntity?>
    
    @Query("SELECT * FROM sources WHERE name = :name")
    suspend fun getSourceByName(name: String): SourceEntity?
    
    @Query("SELECT * FROM sources WHERE type = :type ORDER BY priority DESC, name ASC")
    fun getSourcesByType(type: String): Flow<List<SourceEntity>>
    
    @Query("SELECT * FROM sources WHERE is_enabled = 1 ORDER BY priority DESC, name ASC")
    fun getEnabledSources(): Flow<List<SourceEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SourceEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<SourceEntity>): List<Long>
    
    @Update
    suspend fun updateSource(source: SourceEntity)
    
    @Delete
    suspend fun deleteSource(source: SourceEntity)
    
    @Query("DELETE FROM sources WHERE id = :id")
    suspend fun deleteSourceById(id: Long)
    
    @Query("DELETE FROM sources")
    suspend fun deleteAllSources()
    
    @Query("UPDATE sources SET is_enabled = :isEnabled WHERE id = :id")
    suspend fun updateSourceEnabled(id: Long, isEnabled: Boolean)
    
    @Query("SELECT COUNT(*) FROM sources")
    suspend fun getSourceCount(): Int
}
