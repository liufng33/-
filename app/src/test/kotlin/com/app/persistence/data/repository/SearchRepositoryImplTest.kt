package com.app.persistence.data.repository

import com.app.persistence.data.cache.CacheManager
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.local.database.entity.SourceEntity
import com.app.persistence.data.remote.dto.SearchResponseDto
import com.app.persistence.data.remote.dto.VideoDto
import com.app.persistence.data.remote.source.SearchRemoteDataSource
import com.app.persistence.domain.model.Result
import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.SourceType
import com.app.persistence.domain.repository.SearchOptions
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchRepositoryImplTest {
    
    @MockK
    private lateinit var sourceDao: SourceDao
    
    @MockK
    private lateinit var searchRemoteDataSource: SearchRemoteDataSource
    
    @MockK
    private lateinit var cacheManager: CacheManager
    
    private lateinit var repository: SearchRepositoryImpl
    
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = SearchRepositoryImpl(sourceDao, searchRemoteDataSource, cacheManager)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `search returns cached result when available`() = runTest {
        // Given
        val source = createTestSource()
        val options = SearchOptions(query = "test", limit = 10, offset = 0)
        val cachedResult = com.app.persistence.domain.repository.SearchResult(
            items = emptyList(),
            total = 0,
            hasMore = false
        )
        
        coEvery { cacheManager.get<com.app.persistence.domain.repository.SearchResult>(any()) } returns cachedResult
        
        // When
        val result = repository.search(source, options)
        
        // Then
        assertEquals(cachedResult, result)
        coVerify(exactly = 0) { searchRemoteDataSource.search(any(), any(), any(), any(), any()) }
    }
    
    @Test
    fun `search fetches from remote when cache miss`() = runTest {
        // Given
        val source = createTestSource()
        val options = SearchOptions(query = "test", limit = 10, offset = 0)
        val videoDto = VideoDto(
            id = "1",
            title = "Test Video",
            url = "https://example.com/video/1",
            sourceId = "1"
        )
        val searchResponse = SearchResponseDto(
            items = listOf(videoDto),
            total = 1,
            hasMore = false
        )
        
        coEvery { cacheManager.get<com.app.persistence.domain.repository.SearchResult>(any()) } returns null
        coEvery { searchRemoteDataSource.search(any(), any(), any(), any(), any()) } returns Result.Success(searchResponse)
        coEvery { cacheManager.put(any(), any<com.app.persistence.domain.repository.SearchResult>(), any()) } just Runs
        
        // When
        val result = repository.search(source, options)
        
        // Then
        assertEquals(1, result.items.size)
        assertEquals("Test Video", result.items[0].title)
        coVerify { cacheManager.put(any(), any<com.app.persistence.domain.repository.SearchResult>(), any()) }
    }
    
    @Test
    fun `search returns empty result on remote error`() = runTest {
        // Given
        val source = createTestSource()
        val options = SearchOptions(query = "test", limit = 10, offset = 0)
        
        coEvery { cacheManager.get<com.app.persistence.domain.repository.SearchResult>(any()) } returns null
        coEvery { searchRemoteDataSource.search(any(), any(), any(), any(), any()) } returns 
            Result.Error(com.app.persistence.domain.model.DataError.NetworkError("Network error"))
        
        // When
        val result = repository.search(source, options)
        
        // Then
        assertTrue(result.items.isEmpty())
        assertEquals(0, result.total)
        assertFalse(result.hasMore)
    }
    
    @Test
    fun `getActiveSearchSources returns enabled sources`() = runTest {
        // Given
        val enabledSource = SourceEntity(
            id = 1,
            name = "Enabled Source",
            type = SourceType.SEARCH.name,
            baseUrl = "https://api.example.com",
            parserClass = "com.example.Parser",
            isEnabled = true,
            priority = 1
        )
        val disabledSource = enabledSource.copy(id = 2, name = "Disabled Source", isEnabled = false)
        
        every { sourceDao.getSourcesByType(SourceType.SEARCH.name) } returns flowOf(listOf(enabledSource, disabledSource))
        
        // When
        val result = repository.getActiveSearchSources()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Enabled Source", result[0].name)
        assertTrue(result[0].isEnabled)
    }
    
    @Test
    fun `healthCheck returns true when remote check succeeds`() = runTest {
        // Given
        val source = createTestSource()
        coEvery { searchRemoteDataSource.healthCheck(any()) } returns Result.Success(true)
        
        // When
        val result = repository.healthCheck(source)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `healthCheck returns false when remote check fails`() = runTest {
        // Given
        val source = createTestSource()
        coEvery { searchRemoteDataSource.healthCheck(any()) } returns 
            Result.Error(com.app.persistence.domain.model.DataError.NetworkError("Connection failed"))
        
        // When
        val result = repository.healthCheck(source)
        
        // Then
        assertFalse(result)
    }
    
    private fun createTestSource() = Source(
        id = 1,
        name = "Test Source",
        type = SourceType.SEARCH,
        baseUrl = "https://api.example.com",
        parserClass = "com.example.Parser",
        isEnabled = true,
        priority = 1
    )
}
