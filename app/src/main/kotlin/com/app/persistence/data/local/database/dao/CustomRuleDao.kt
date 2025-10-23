package com.app.persistence.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.persistence.data.local.database.entity.CustomRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomRuleDao {
    
    @Query("SELECT * FROM custom_rules ORDER BY priority DESC, name ASC")
    fun getAllRules(): Flow<List<CustomRuleEntity>>
    
    @Query("SELECT * FROM custom_rules WHERE id = :id")
    suspend fun getRuleById(id: Long): CustomRuleEntity?
    
    @Query("SELECT * FROM custom_rules WHERE id = :id")
    fun getRuleByIdFlow(id: Long): Flow<CustomRuleEntity?>
    
    @Query("SELECT * FROM custom_rules WHERE source_id = :sourceId ORDER BY priority DESC, name ASC")
    fun getRulesBySourceId(sourceId: Long): Flow<List<CustomRuleEntity>>
    
    @Query("SELECT * FROM custom_rules WHERE rule_type = :ruleType ORDER BY priority DESC, name ASC")
    fun getRulesByType(ruleType: String): Flow<List<CustomRuleEntity>>
    
    @Query("SELECT * FROM custom_rules WHERE source_id = :sourceId AND is_enabled = 1 ORDER BY priority DESC, name ASC")
    fun getEnabledRulesBySourceId(sourceId: Long): Flow<List<CustomRuleEntity>>
    
    @Query("SELECT * FROM custom_rules WHERE is_enabled = 1 ORDER BY priority DESC, name ASC")
    fun getEnabledRules(): Flow<List<CustomRuleEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: CustomRuleEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<CustomRuleEntity>): List<Long>
    
    @Update
    suspend fun updateRule(rule: CustomRuleEntity)
    
    @Delete
    suspend fun deleteRule(rule: CustomRuleEntity)
    
    @Query("DELETE FROM custom_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long)
    
    @Query("DELETE FROM custom_rules WHERE source_id = :sourceId")
    suspend fun deleteRulesBySourceId(sourceId: Long)
    
    @Query("DELETE FROM custom_rules")
    suspend fun deleteAllRules()
    
    @Query("UPDATE custom_rules SET is_enabled = :isEnabled WHERE id = :id")
    suspend fun updateRuleEnabled(id: Long, isEnabled: Boolean)
}
