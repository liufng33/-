package com.app.persistence.domain.repository

import com.app.persistence.domain.model.CustomRule
import com.app.persistence.domain.model.RuleType
import kotlinx.coroutines.flow.Flow

interface CustomRuleRepository {
    
    fun getAllRules(): Flow<List<CustomRule>>
    
    suspend fun getRuleById(id: Long): CustomRule?
    
    fun getRuleByIdFlow(id: Long): Flow<CustomRule?>
    
    fun getRulesBySourceId(sourceId: Long): Flow<List<CustomRule>>
    
    fun getRulesByType(ruleType: RuleType): Flow<List<CustomRule>>
    
    fun getEnabledRulesBySourceId(sourceId: Long): Flow<List<CustomRule>>
    
    fun getEnabledRules(): Flow<List<CustomRule>>
    
    suspend fun insertRule(rule: CustomRule): Long
    
    suspend fun insertRules(rules: List<CustomRule>): List<Long>
    
    suspend fun updateRule(rule: CustomRule)
    
    suspend fun deleteRule(rule: CustomRule)
    
    suspend fun deleteRuleById(id: Long)
    
    suspend fun deleteRulesBySourceId(sourceId: Long)
    
    suspend fun updateRuleEnabled(id: Long, isEnabled: Boolean)
}
