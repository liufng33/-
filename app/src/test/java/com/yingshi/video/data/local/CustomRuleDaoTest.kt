package com.yingshi.video.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yingshi.video.data.local.dao.CustomRuleDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.database.YingshiDatabase
import com.yingshi.video.data.local.entity.CustomRuleEntity
import com.yingshi.video.data.local.entity.SearchSourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CustomRuleDaoTest {

    private lateinit var database: YingshiDatabase
    private lateinit var customRuleDao: CustomRuleDao
    private lateinit var searchSourceDao: SearchSourceDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            YingshiDatabase::class.java
        ).allowMainThreadQueries().build()
        
        customRuleDao = database.customRuleDao()
        searchSourceDao = database.searchSourceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetRule() = runTest {
        val source = createTestSource("source1", "Test Source")
        searchSourceDao.insertSource(source)
        
        val rule = createTestRule("rule1", "source1", "Title Rule")
        customRuleDao.insertRule(rule)
        
        val result = customRuleDao.getRuleById("rule1")
        assert(result != null)
        assert(result?.name == "Title Rule")
    }

    @Test
    fun getRulesBySourceId() = runTest {
        val source = createTestSource("source1", "Test Source")
        searchSourceDao.insertSource(source)
        
        val rules = listOf(
            createTestRule("rule1", "source1", "Rule 1"),
            createTestRule("rule2", "source1", "Rule 2"),
            createTestRule("rule3", "source1", "Rule 3")
        )
        
        customRuleDao.insertRules(rules)
        val result = customRuleDao.getRulesBySourceId("source1").first()
        
        assert(result.size == 3)
    }

    @Test
    fun deleteRulesBySourceId() = runTest {
        val source = createTestSource("source1", "Test Source")
        searchSourceDao.insertSource(source)
        
        val rules = listOf(
            createTestRule("rule1", "source1", "Rule 1"),
            createTestRule("rule2", "source1", "Rule 2")
        )
        
        customRuleDao.insertRules(rules)
        customRuleDao.deleteRulesBySourceId("source1")
        
        val result = customRuleDao.getRulesBySourceId("source1").first()
        assert(result.isEmpty())
    }

    @Test
    fun cascadeDeleteRulesWhenSourceDeleted() = runTest {
        val source = createTestSource("source1", "Test Source")
        searchSourceDao.insertSource(source)
        
        val rule = createTestRule("rule1", "source1", "Rule 1")
        customRuleDao.insertRule(rule)
        
        searchSourceDao.deleteSourceById("source1")
        
        val result = customRuleDao.getRuleById("rule1")
        assert(result == null)
    }

    @Test
    fun updateRule() = runTest {
        val source = createTestSource("source1", "Test Source")
        searchSourceDao.insertSource(source)
        
        val rule = createTestRule("rule1", "source1", "Original Name")
        customRuleDao.insertRule(rule)
        
        val updated = rule.copy(name = "Updated Name")
        customRuleDao.updateRule(updated)
        
        val result = customRuleDao.getRuleById("rule1")
        assert(result?.name == "Updated Name")
    }

    private fun createTestSource(id: String, name: String) = SearchSourceEntity(
        id = id,
        name = name,
        apiEndpoint = "https://example.com/api",
        isEnabled = true,
        priority = 0
    )

    private fun createTestRule(
        id: String,
        sourceId: String,
        name: String
    ) = CustomRuleEntity(
        id = id,
        sourceId = sourceId,
        name = name,
        ruleType = "CSS_SELECTOR",
        selector = "div.title",
        attribute = "text",
        isRequired = true
    )
}
