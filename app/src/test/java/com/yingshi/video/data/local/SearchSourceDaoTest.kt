package com.yingshi.video.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.database.YingshiDatabase
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
class SearchSourceDaoTest {

    private lateinit var database: YingshiDatabase
    private lateinit var searchSourceDao: SearchSourceDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            YingshiDatabase::class.java
        ).allowMainThreadQueries().build()
        
        searchSourceDao = database.searchSourceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetSource() = runTest {
        val source = createTestSource("1", "Test Source")
        
        searchSourceDao.insertSource(source)
        val result = searchSourceDao.getSourceById("1")
        
        assert(result != null)
        assert(result?.name == "Test Source")
    }

    @Test
    fun insertMultipleSources() = runTest {
        val sources = listOf(
            createTestSource("1", "Source 1"),
            createTestSource("2", "Source 2"),
            createTestSource("3", "Source 3")
        )
        
        searchSourceDao.insertSources(sources)
        val result = searchSourceDao.getAllSources().first()
        
        assert(result.size == 3)
    }

    @Test
    fun updateSource() = runTest {
        val source = createTestSource("1", "Original Name")
        searchSourceDao.insertSource(source)
        
        val updated = source.copy(name = "Updated Name")
        searchSourceDao.updateSource(updated)
        
        val result = searchSourceDao.getSourceById("1")
        assert(result?.name == "Updated Name")
    }

    @Test
    fun deleteSource() = runTest {
        val source = createTestSource("1", "Test Source")
        searchSourceDao.insertSource(source)
        
        searchSourceDao.deleteSourceById("1")
        val result = searchSourceDao.getSourceById("1")
        
        assert(result == null)
    }

    @Test
    fun getEnabledSourcesOnly() = runTest {
        val sources = listOf(
            createTestSource("1", "Enabled Source", isEnabled = true),
            createTestSource("2", "Disabled Source", isEnabled = false),
            createTestSource("3", "Another Enabled", isEnabled = true)
        )
        
        searchSourceDao.insertSources(sources)
        val result = searchSourceDao.getEnabledSources().first()
        
        assert(result.size == 2)
        assert(result.all { it.isEnabled })
    }

    @Test
    fun setSourceEnabled() = runTest {
        val source = createTestSource("1", "Test Source", isEnabled = true)
        searchSourceDao.insertSource(source)
        
        searchSourceDao.setSourceEnabled("1", false)
        val result = searchSourceDao.getSourceById("1")
        
        assert(result?.isEnabled == false)
    }

    @Test
    fun getSourceCount() = runTest {
        val sources = listOf(
            createTestSource("1", "Source 1"),
            createTestSource("2", "Source 2")
        )
        
        searchSourceDao.insertSources(sources)
        val count = searchSourceDao.getSourceCount()
        
        assert(count == 2)
    }

    private fun createTestSource(
        id: String,
        name: String,
        isEnabled: Boolean = true
    ) = SearchSourceEntity(
        id = id,
        name = name,
        apiEndpoint = "https://example.com/api",
        isEnabled = isEnabled,
        priority = 0,
        headers = emptyMap(),
        description = "Test description"
    )
}
