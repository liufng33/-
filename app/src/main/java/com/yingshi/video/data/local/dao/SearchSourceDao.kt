package com.yingshi.video.data.local.dao

import androidx.room.*
import com.yingshi.video.data.local.entity.SearchSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchSourceDao {
    @Query("SELECT * FROM search_sources ORDER BY priority DESC, name ASC")
    fun getAllSources(): Flow<List<SearchSourceEntity>>

    @Query("SELECT * FROM search_sources WHERE isEnabled = 1 ORDER BY priority DESC, name ASC")
    fun getEnabledSources(): Flow<List<SearchSourceEntity>>

    @Query("SELECT * FROM search_sources WHERE id = :id")
    suspend fun getSourceById(id: String): SearchSourceEntity?

    @Query("SELECT * FROM search_sources WHERE id = :id")
    fun getSourceByIdFlow(id: String): Flow<SearchSourceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SearchSourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<SearchSourceEntity>)

    @Update
    suspend fun updateSource(source: SearchSourceEntity)

    @Delete
    suspend fun deleteSource(source: SearchSourceEntity)

    @Query("DELETE FROM search_sources WHERE id = :id")
    suspend fun deleteSourceById(id: String)

    @Query("DELETE FROM search_sources")
    suspend fun deleteAllSources()

    @Query("UPDATE search_sources SET isEnabled = :enabled WHERE id = :id")
    suspend fun setSourceEnabled(id: String, enabled: Boolean)

    @Query("UPDATE search_sources SET priority = :priority WHERE id = :id")
    suspend fun setSourcePriority(id: String, priority: Int)

    @Query("SELECT COUNT(*) FROM search_sources")
    suspend fun getSourceCount(): Int
}
