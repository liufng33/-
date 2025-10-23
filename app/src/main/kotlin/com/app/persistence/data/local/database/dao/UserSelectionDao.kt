package com.app.persistence.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.persistence.data.local.database.entity.UserSelectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSelectionDao {
    
    @Query("SELECT * FROM user_selections ORDER BY last_accessed_at DESC")
    fun getAllSelections(): Flow<List<UserSelectionEntity>>
    
    @Query("SELECT * FROM user_selections WHERE id = :id")
    suspend fun getSelectionById(id: Long): UserSelectionEntity?
    
    @Query("SELECT * FROM user_selections WHERE id = :id")
    fun getSelectionByIdFlow(id: Long): Flow<UserSelectionEntity?>
    
    @Query("SELECT * FROM user_selections WHERE source_id = :sourceId ORDER BY last_accessed_at DESC")
    fun getSelectionsBySourceId(sourceId: Long): Flow<List<UserSelectionEntity>>
    
    @Query("SELECT * FROM user_selections WHERE item_type = :itemType ORDER BY last_accessed_at DESC")
    fun getSelectionsByType(itemType: String): Flow<List<UserSelectionEntity>>
    
    @Query("SELECT * FROM user_selections WHERE source_id = :sourceId AND item_id = :itemId")
    suspend fun getSelectionBySourceAndItem(sourceId: Long, itemId: String): UserSelectionEntity?
    
    @Query("SELECT * FROM user_selections WHERE item_type = :itemType ORDER BY selected_at DESC LIMIT :limit")
    fun getRecentSelectionsByType(itemType: String, limit: Int = 20): Flow<List<UserSelectionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelection(selection: UserSelectionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelections(selections: List<UserSelectionEntity>): List<Long>
    
    @Update
    suspend fun updateSelection(selection: UserSelectionEntity)
    
    @Delete
    suspend fun deleteSelection(selection: UserSelectionEntity)
    
    @Query("DELETE FROM user_selections WHERE id = :id")
    suspend fun deleteSelectionById(id: Long)
    
    @Query("DELETE FROM user_selections WHERE source_id = :sourceId")
    suspend fun deleteSelectionsBySourceId(sourceId: Long)
    
    @Query("DELETE FROM user_selections WHERE item_type = :itemType")
    suspend fun deleteSelectionsByType(itemType: String)
    
    @Query("DELETE FROM user_selections")
    suspend fun deleteAllSelections()
    
    @Query("UPDATE user_selections SET last_accessed_at = :timestamp WHERE id = :id")
    suspend fun updateLastAccessedTime(id: Long, timestamp: Long = System.currentTimeMillis())
}
