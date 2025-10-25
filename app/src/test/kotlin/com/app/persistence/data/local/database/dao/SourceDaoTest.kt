package com.app.persistence.data.local.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.app.persistence.data.local.database.AppDatabase
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
class SourceDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AppDatabase
    private lateinit var sourceDao: SourceDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        sourceDao = database.sourceDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `insertSource should insert and return source id`() = runTest {
        val source = SourceEntity(
            name = "Test Source",
            type = "SEARCH",
            baseUrl = "https://test.com",
            parserClass = "com.test.Parser"
        )
        
        val id = sourceDao.insertSource(source)
        
        assertTrue(id > 0)
        val retrieved = sourceDao.getSourceById(id)
        assertNotNull(retrieved)
        assertEquals(source.name, retrieved.name)
    }
    
    @Test
    fun `getAllSources should return all sources ordered by priority`() = runTest {
        val sources = listOf(
            SourceEntity(name = "Source A", type = "SEARCH", baseUrl = "https://a.com", parserClass = "A", priority = 50),
            SourceEntity(name = "Source B", type = "PARSER", baseUrl = "https://b.com", parserClass = "B", priority = 100),
            SourceEntity(name = "Source C", type = "HYBRID", baseUrl = "https://c.com", parserClass = "C", priority = 75)
        )
        
        sourceDao.insertSources(sources)
        
        val retrieved = sourceDao.getAllSources().first()
        assertEquals(3, retrieved.size)
        assertEquals("Source B", retrieved[0].name)
        assertEquals("Source C", retrieved[1].name)
        assertEquals("Source A", retrieved[2].name)
    }
    
    @Test
    fun `getSourceByName should return correct source`() = runTest {
        val source = SourceEntity(
            name = "Unique Source",
            type = "SEARCH",
            baseUrl = "https://unique.com",
            parserClass = "com.unique.Parser"
        )
        
        sourceDao.insertSource(source)
        
        val retrieved = sourceDao.getSourceByName("Unique Source")
        assertNotNull(retrieved)
        assertEquals("Unique Source", retrieved.name)
    }
    
    @Test
    fun `getSourcesByType should return only sources of specified type`() = runTest {
        val sources = listOf(
            SourceEntity(name = "Search 1", type = "SEARCH", baseUrl = "https://s1.com", parserClass = "S1"),
            SourceEntity(name = "Parser 1", type = "PARSER", baseUrl = "https://p1.com", parserClass = "P1"),
            SourceEntity(name = "Search 2", type = "SEARCH", baseUrl = "https://s2.com", parserClass = "S2")
        )
        
        sourceDao.insertSources(sources)
        
        val searchSources = sourceDao.getSourcesByType("SEARCH").first()
        assertEquals(2, searchSources.size)
        assertTrue(searchSources.all { it.type == "SEARCH" })
    }
    
    @Test
    fun `getEnabledSources should return only enabled sources`() = runTest {
        val sources = listOf(
            SourceEntity(name = "Enabled", type = "SEARCH", baseUrl = "https://e.com", parserClass = "E", isEnabled = true),
            SourceEntity(name = "Disabled", type = "SEARCH", baseUrl = "https://d.com", parserClass = "D", isEnabled = false)
        )
        
        sourceDao.insertSources(sources)
        
        val enabledSources = sourceDao.getEnabledSources().first()
        assertEquals(1, enabledSources.size)
        assertEquals("Enabled", enabledSources[0].name)
    }
    
    @Test
    fun `updateSource should modify existing source`() = runTest {
        val source = SourceEntity(
            name = "Original",
            type = "SEARCH",
            baseUrl = "https://original.com",
            parserClass = "Original"
        )
        
        val id = sourceDao.insertSource(source)
        val updated = source.copy(id = id, name = "Updated")
        sourceDao.updateSource(updated)
        
        val retrieved = sourceDao.getSourceById(id)
        assertNotNull(retrieved)
        assertEquals("Updated", retrieved.name)
    }
    
    @Test
    fun `deleteSource should remove source`() = runTest {
        val source = SourceEntity(
            name = "To Delete",
            type = "SEARCH",
            baseUrl = "https://delete.com",
            parserClass = "Delete"
        )
        
        val id = sourceDao.insertSource(source)
        sourceDao.deleteSourceById(id)
        
        val retrieved = sourceDao.getSourceById(id)
        assertNull(retrieved)
    }
    
    @Test
    fun `updateSourceEnabled should change enabled state`() = runTest {
        val source = SourceEntity(
            name = "Toggle",
            type = "SEARCH",
            baseUrl = "https://toggle.com",
            parserClass = "Toggle",
            isEnabled = true
        )
        
        val id = sourceDao.insertSource(source)
        sourceDao.updateSourceEnabled(id, false)
        
        val retrieved = sourceDao.getSourceById(id)
        assertNotNull(retrieved)
        assertEquals(false, retrieved.isEnabled)
    }
    
    @Test
    fun `getSourceCount should return correct count`() = runTest {
        val sources = listOf(
            SourceEntity(name = "S1", type = "SEARCH", baseUrl = "https://s1.com", parserClass = "S1"),
            SourceEntity(name = "S2", type = "SEARCH", baseUrl = "https://s2.com", parserClass = "S2"),
            SourceEntity(name = "S3", type = "SEARCH", baseUrl = "https://s3.com", parserClass = "S3")
        )
        
        sourceDao.insertSources(sources)
        
        val count = sourceDao.getSourceCount()
        assertEquals(3, count)
    }
}
