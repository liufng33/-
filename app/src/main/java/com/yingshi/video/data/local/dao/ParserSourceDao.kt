package com.yingshi.video.data.local.dao

import androidx.room.*
import com.yingshi.video.data.local.entity.ParserSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParserSourceDao {
    @Query("SELECT * FROM parser_sources ORDER BY priority DESC, name ASC")
    fun getAllParsers(): Flow<List<ParserSourceEntity>>

    @Query("SELECT * FROM parser_sources WHERE isEnabled = 1 ORDER BY priority DESC, name ASC")
    fun getEnabledParsers(): Flow<List<ParserSourceEntity>>

    @Query("SELECT * FROM parser_sources WHERE id = :id")
    suspend fun getParserById(id: String): ParserSourceEntity?

    @Query("SELECT * FROM parser_sources WHERE id = :id")
    fun getParserByIdFlow(id: String): Flow<ParserSourceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParser(parser: ParserSourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParsers(parsers: List<ParserSourceEntity>)

    @Update
    suspend fun updateParser(parser: ParserSourceEntity)

    @Delete
    suspend fun deleteParser(parser: ParserSourceEntity)

    @Query("DELETE FROM parser_sources WHERE id = :id")
    suspend fun deleteParserById(id: String)

    @Query("DELETE FROM parser_sources")
    suspend fun deleteAllParsers()

    @Query("UPDATE parser_sources SET isEnabled = :enabled WHERE id = :id")
    suspend fun setParserEnabled(id: String, enabled: Boolean)

    @Query("UPDATE parser_sources SET priority = :priority WHERE id = :id")
    suspend fun setParserPriority(id: String, priority: Int)

    @Query("SELECT COUNT(*) FROM parser_sources")
    suspend fun getParserCount(): Int

    @Query("SELECT * FROM parser_sources WHERE isEnabled = 1 AND supportedDomains LIKE '%' || :domain || '%' ORDER BY priority DESC")
    fun getParsersByDomain(domain: String): Flow<List<ParserSourceEntity>>
}
