package com.app.persistence.data.local.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.app.persistence.data.local.database.AppDatabase
import com.app.persistence.data.local.database.entity.SourceEntity
import com.app.persistence.data.local.database.entity.UserSelectionEntity
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
class UserSelectionDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AppDatabase
    private lateinit var userSelectionDao: UserSelectionDao
    private lateinit var sourceDao: SourceDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        userSelectionDao = database.userSelectionDao()
        sourceDao = database.sourceDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `insertSelection should insert and return selection id`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selection = UserSelectionEntity(
            sourceId = sourceId,
            itemId = "item123",
            itemType = "FAVORITE",
            title = "Test Selection"
        )
        
        val id = userSelectionDao.insertSelection(selection)
        
        assertTrue(id > 0)
        val retrieved = userSelectionDao.getSelectionById(id)
        assertNotNull(retrieved)
        assertEquals(selection.title, retrieved.title)
    }
    
    @Test
    fun `getAllSelections should return all selections ordered by last accessed`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val now = System.currentTimeMillis()
        val selections = listOf(
            UserSelectionEntity(sourceId = sourceId, itemId = "1", itemType = "FAVORITE", title = "Old", lastAccessedAt = now - 2000),
            UserSelectionEntity(sourceId = sourceId, itemId = "2", itemType = "FAVORITE", title = "New", lastAccessedAt = now),
            UserSelectionEntity(sourceId = sourceId, itemId = "3", itemType = "FAVORITE", title = "Middle", lastAccessedAt = now - 1000)
        )
        
        userSelectionDao.insertSelections(selections)
        
        val retrieved = userSelectionDao.getAllSelections().first()
        assertEquals(3, retrieved.size)
        assertEquals("New", retrieved[0].title)
        assertEquals("Middle", retrieved[1].title)
        assertEquals("Old", retrieved[2].title)
    }
    
    @Test
    fun `getSelectionsBySourceId should return only selections for specified source`() = runTest {
        val source1Id = sourceDao.insertSource(
            SourceEntity(name = "Source 1", type = "SEARCH", baseUrl = "https://s1.com", parserClass = "S1")
        )
        val source2Id = sourceDao.insertSource(
            SourceEntity(name = "Source 2", type = "SEARCH", baseUrl = "https://s2.com", parserClass = "S2")
        )
        
        val selections = listOf(
            UserSelectionEntity(sourceId = source1Id, itemId = "1", itemType = "FAVORITE", title = "S1-1"),
            UserSelectionEntity(sourceId = source2Id, itemId = "2", itemType = "FAVORITE", title = "S2-1"),
            UserSelectionEntity(sourceId = source1Id, itemId = "3", itemType = "FAVORITE", title = "S1-2")
        )
        
        userSelectionDao.insertSelections(selections)
        
        val source1Selections = userSelectionDao.getSelectionsBySourceId(source1Id).first()
        assertEquals(2, source1Selections.size)
        assertTrue(source1Selections.all { it.sourceId == source1Id })
    }
    
    @Test
    fun `getSelectionsByType should return only selections of specified type`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selections = listOf(
            UserSelectionEntity(sourceId = sourceId, itemId = "1", itemType = "FAVORITE", title = "Fav 1"),
            UserSelectionEntity(sourceId = sourceId, itemId = "2", itemType = "BOOKMARK", title = "Book 1"),
            UserSelectionEntity(sourceId = sourceId, itemId = "3", itemType = "FAVORITE", title = "Fav 2")
        )
        
        userSelectionDao.insertSelections(selections)
        
        val favorites = userSelectionDao.getSelectionsByType("FAVORITE").first()
        assertEquals(2, favorites.size)
        assertTrue(favorites.all { it.itemType == "FAVORITE" })
    }
    
    @Test
    fun `getSelectionBySourceAndItem should return correct selection`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selection = UserSelectionEntity(
            sourceId = sourceId,
            itemId = "unique-item",
            itemType = "FAVORITE",
            title = "Unique Selection"
        )
        
        userSelectionDao.insertSelection(selection)
        
        val retrieved = userSelectionDao.getSelectionBySourceAndItem(sourceId, "unique-item")
        assertNotNull(retrieved)
        assertEquals("Unique Selection", retrieved.title)
    }
    
    @Test
    fun `getRecentSelectionsByType should return limited selections`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selections = (1..25).map { i ->
            UserSelectionEntity(
                sourceId = sourceId,
                itemId = "item$i",
                itemType = "HISTORY",
                title = "Item $i",
                selectedAt = System.currentTimeMillis() + i
            )
        }
        
        userSelectionDao.insertSelections(selections)
        
        val recent = userSelectionDao.getRecentSelectionsByType("HISTORY", 10).first()
        assertEquals(10, recent.size)
    }
    
    @Test
    fun `deleteSelection should remove selection`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selection = UserSelectionEntity(
            sourceId = sourceId,
            itemId = "delete-me",
            itemType = "FAVORITE",
            title = "To Delete"
        )
        
        val id = userSelectionDao.insertSelection(selection)
        userSelectionDao.deleteSelectionById(id)
        
        val retrieved = userSelectionDao.getSelectionById(id)
        assertNull(retrieved)
    }
    
    @Test
    fun `deleteSelectionsBySourceId should remove all selections for source`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selections = listOf(
            UserSelectionEntity(sourceId = sourceId, itemId = "1", itemType = "FAVORITE", title = "S1"),
            UserSelectionEntity(sourceId = sourceId, itemId = "2", itemType = "FAVORITE", title = "S2")
        )
        
        userSelectionDao.insertSelections(selections)
        userSelectionDao.deleteSelectionsBySourceId(sourceId)
        
        val retrieved = userSelectionDao.getSelectionsBySourceId(sourceId).first()
        assertEquals(0, retrieved.size)
    }
    
    @Test
    fun `updateLastAccessedTime should update timestamp`() = runTest {
        val sourceId = sourceDao.insertSource(
            SourceEntity(name = "Test Source", type = "SEARCH", baseUrl = "https://test.com", parserClass = "Test")
        )
        
        val selection = UserSelectionEntity(
            sourceId = sourceId,
            itemId = "item",
            itemType = "FAVORITE",
            title = "Test",
            lastAccessedAt = 1000L
        )
        
        val id = userSelectionDao.insertSelection(selection)
        val newTimestamp = System.currentTimeMillis()
        userSelectionDao.updateLastAccessedTime(id, newTimestamp)
        
        val retrieved = userSelectionDao.getSelectionById(id)
        assertNotNull(retrieved)
        assertTrue(retrieved.lastAccessedAt > 1000L)
    }
}
