package com.yingshi.video.data.local.dao

import androidx.room.*
import com.yingshi.video.data.local.entity.CustomRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomRuleDao {
    @Query("SELECT * FROM custom_rules")
    fun getAllRules(): Flow<List<CustomRuleEntity>>

    @Query("SELECT * FROM custom_rules WHERE sourceId = :sourceId")
    fun getRulesBySourceId(sourceId: String): Flow<List<CustomRuleEntity>>

    @Query("SELECT * FROM custom_rules WHERE id = :id")
    suspend fun getRuleById(id: String): CustomRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: CustomRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<CustomRuleEntity>)

    @Update
    suspend fun updateRule(rule: CustomRuleEntity)

    @Delete
    suspend fun deleteRule(rule: CustomRuleEntity)

    @Query("DELETE FROM custom_rules WHERE id = :id")
    suspend fun deleteRuleById(id: String)

    @Query("DELETE FROM custom_rules WHERE sourceId = :sourceId")
    suspend fun deleteRulesBySourceId(sourceId: String)

    @Query("DELETE FROM custom_rules")
    suspend fun deleteAllRules()
}
