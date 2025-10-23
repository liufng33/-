package com.cleanarch.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cleanarch.data.source.local.database.entity.SampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleDao {
    @Query("SELECT * FROM sample")
    fun getAll(): Flow<List<SampleEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SampleEntity)
    
    @Query("DELETE FROM sample")
    suspend fun deleteAll()
}
