package com.app.persistence.data.repository

import com.app.persistence.data.local.database.dao.CustomRuleDao
import com.app.persistence.data.local.database.entity.CustomRuleEntity
import com.app.persistence.domain.model.CustomRule
import com.app.persistence.domain.model.RuleType
import com.app.persistence.domain.repository.CustomRuleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomRuleRepositoryImpl @Inject constructor(
    private val customRuleDao: CustomRuleDao
) : CustomRuleRepository {
    
    override fun getAllRules(): Flow<List<CustomRule>> {
        return customRuleDao.getAllRules().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getRuleById(id: Long): CustomRule? {
        return customRuleDao.getRuleById(id)?.toDomain()
    }
    
    override fun getRuleByIdFlow(id: Long): Flow<CustomRule?> {
        return customRuleDao.getRuleByIdFlow(id).map { it?.toDomain() }
    }
    
    override fun getRulesBySourceId(sourceId: Long): Flow<List<CustomRule>> {
        return customRuleDao.getRulesBySourceId(sourceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getRulesByType(ruleType: RuleType): Flow<List<CustomRule>> {
        return customRuleDao.getRulesByType(ruleType.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getEnabledRulesBySourceId(sourceId: Long): Flow<List<CustomRule>> {
        return customRuleDao.getEnabledRulesBySourceId(sourceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getEnabledRules(): Flow<List<CustomRule>> {
        return customRuleDao.getEnabledRules().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun insertRule(rule: CustomRule): Long {
        return customRuleDao.insertRule(CustomRuleEntity.fromDomain(rule))
    }
    
    override suspend fun insertRules(rules: List<CustomRule>): List<Long> {
        return customRuleDao.insertRules(rules.map { CustomRuleEntity.fromDomain(it) })
    }
    
    override suspend fun updateRule(rule: CustomRule) {
        customRuleDao.updateRule(CustomRuleEntity.fromDomain(rule))
    }
    
    override suspend fun deleteRule(rule: CustomRule) {
        customRuleDao.deleteRule(CustomRuleEntity.fromDomain(rule))
    }
    
    override suspend fun deleteRuleById(id: Long) {
        customRuleDao.deleteRuleById(id)
    }
    
    override suspend fun deleteRulesBySourceId(sourceId: Long) {
        customRuleDao.deleteRulesBySourceId(sourceId)
    }
    
    override suspend fun updateRuleEnabled(id: Long, isEnabled: Boolean) {
        customRuleDao.updateRuleEnabled(id, isEnabled)
    }
}
