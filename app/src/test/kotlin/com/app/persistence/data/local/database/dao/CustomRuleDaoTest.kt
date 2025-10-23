package com.app.persistence.data.local.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.app.persistence.data.local.database.AppDatabase
import com.app.persistence.data.local.database.entity.CustomRuleEntity
import com.app.persistence.data.local.database.entity.SourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CustomRuleDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AppDatabase
    private lateinit var customRuleDao: CustomRuleDao
    private lateinit var sourceDao: SourceDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        customRuleDao = database.customRuleDao()
        sourceDao = database.sourceDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `insertRule should insert and return rule id`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rule = CustomRuleEntity(
            sourceId = sourceId,
            name = "Test Rule",
            ruleType = "FILTER",
            pattern = "test.*"
        )
        
        val id = customRuleDao.insertRule(rule)
        
        assertTrue(id > 0)
        val retrieved = customRuleDao.getRuleById(id)
        assertNotNull(retrieved)
        assertEquals(rule.name, retrieved.name)
    }
    
    @Test
    fun `getAllRules should return all rules ordered by priority`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rules = listOf(
            CustomRuleEntity(sourceId = sourceId, name = "Rule A", ruleType = "FILTER", pattern = "a.*", priority = 50),
            CustomRuleEntity(sourceId = sourceId, name = "Rule B", ruleType = "TRANSFORM", pattern = "b.*", priority = 100),
            CustomRuleEntity(sourceId = sourceId, name = "Rule C", ruleType = "VALIDATION", pattern = "c.*", priority = 75)
        )
        
        customRuleDao.insertRules(rules)
        
        val retrieved = customRuleDao.getAllRules().first()
        assertEquals(3, retrieved.size)
        assertEquals("Rule B", retrieved[0].name)
        assertEquals("Rule C", retrieved[1].name)
        assertEquals("Rule A", retrieved[2].name)
    }
    
    @Test
    fun `getRulesBySourceId should return only rules for specified source`() = runTest {
        val source1Id = sourceDao.insertSource(
            SourceEntity(name = "Source 1", type = "SEARCH", baseUrl = "https://s1.com", parserClass = "S1")
        )
        val source2Id = sourceDao.insertSource(
            SourceEntity(name = "Source 2", type = "SEARCH", baseUrl = "https://s2.com", parserClass = "S2")
        )
        
        val rules = listOf(
            CustomRuleEntity(sourceId = source1Id, name = "Rule 1", ruleType = "FILTER", pattern = "1.*"),
            CustomRuleEntity(sourceId = source2Id, name = "Rule 2", ruleType = "FILTER", pattern = "2.*"),
            CustomRuleEntity(sourceId = source1Id, name = "Rule 3", ruleType = "FILTER", pattern = "3.*")
        )
        
        customRuleDao.insertRules(rules)
        
        val source1Rules = customRuleDao.getRulesBySourceId(source1Id).first()
        assertEquals(2, source1Rules.size)
        assertTrue(source1Rules.all { it.sourceId == source1Id })
    }
    
    @Test
    fun `getRulesByType should return only rules of specified type`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rules = listOf(
            CustomRuleEntity(sourceId = sourceId, name = "Filter 1", ruleType = "FILTER", pattern = "f1.*"),
            CustomRuleEntity(sourceId = sourceId, name = "Transform 1", ruleType = "TRANSFORM", pattern = "t1.*"),
            CustomRuleEntity(sourceId = sourceId, name = "Filter 2", ruleType = "FILTER", pattern = "f2.*")
        )
        
        customRuleDao.insertRules(rules)
        
        val filterRules = customRuleDao.getRulesByType("FILTER").first()
        assertEquals(2, filterRules.size)
        assertTrue(filterRules.all { it.ruleType == "FILTER" })
    }
    
    @Test
    fun `getEnabledRulesBySourceId should return only enabled rules for source`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rules = listOf(
            CustomRuleEntity(sourceId = sourceId, name = "Enabled", ruleType = "FILTER", pattern = "e.*", isEnabled = true),
            CustomRuleEntity(sourceId = sourceId, name = "Disabled", ruleType = "FILTER", pattern = "d.*", isEnabled = false)
        )
        
        customRuleDao.insertRules(rules)
        
        val enabledRules = customRuleDao.getEnabledRulesBySourceId(sourceId).first()
        assertEquals(1, enabledRules.size)
        assertEquals("Enabled", enabledRules[0].name)
    }
    
    @Test
    fun `deleteRule should remove rule`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rule = CustomRuleEntity(
            sourceId = sourceId,
            name = "To Delete",
            ruleType = "FILTER",
            pattern = "delete.*"
        )
        
        val id = customRuleDao.insertRule(rule)
        customRuleDao.deleteRuleById(id)
        
        val retrieved = customRuleDao.getRuleById(id)
        assertNull(retrieved)
    }
    
    @Test
    fun `deleteRulesBySourceId should remove all rules for source`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rules = listOf(
            CustomRuleEntity(sourceId = sourceId, name = "Rule 1", ruleType = "FILTER", pattern = "1.*"),
            CustomRuleEntity(sourceId = sourceId, name = "Rule 2", ruleType = "FILTER", pattern = "2.*")
        )
        
        customRuleDao.insertRules(rules)
        customRuleDao.deleteRulesBySourceId(sourceId)
        
        val retrieved = customRuleDao.getRulesBySourceId(sourceId).first()
        assertEquals(0, retrieved.size)
    }
    
    @Test
    fun `updateRuleEnabled should change enabled state`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val rule = CustomRuleEntity(
            sourceId = sourceId,
            name = "Toggle",
            ruleType = "FILTER",
            pattern = "toggle.*",
            isEnabled = true
        )
        
        val id = customRuleDao.insertRule(rule)
        customRuleDao.updateRuleEnabled(id, false)
        
        val retrieved = customRuleDao.getRuleById(id)
        assertNotNull(retrieved)
        assertEquals(false, retrieved.isEnabled)
    }
}
