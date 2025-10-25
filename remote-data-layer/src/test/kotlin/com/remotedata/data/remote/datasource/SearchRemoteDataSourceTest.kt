package com.remotedata.data.remote.datasource

import com.remotedata.data.remote.api.SearchService
import com.remotedata.data.remote.dto.SearchItemDto
import com.remotedata.data.remote.dto.SearchResponseDto
import com.remotedata.utils.NoOpRateLimiter
import com.remotedata.utils.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class SearchRemoteDataSourceTest {
    
    private lateinit var searchService: SearchService
    private lateinit var dataSource: SearchRemoteDataSource
    
    @BeforeEach
    fun setup() {
        searchService = mockk()
        dataSource = SearchRemoteDataSourceImpl(searchService, NoOpRateLimiter())
    }
    
    @Test
    fun `search returns success with results`() = runTest {
        val dto = SearchResponseDto(
            results = listOf(
                SearchItemDto(
                    id = "1",
                    title = "Test Result",
                    description = "Test description",
                    url = "https://example.com",
                    score = 0.95
                )
            ),
            total = 1,
            page = 1
        )
        coEvery { searchService.search("test", 1, 10) } returns Response.success(dto)
        
        val result = dataSource.search("test", 1, 10)
        
        assertTrue(result is Result.Success)
        val searchResults = (result as Result.Success).data
        assertEquals(1, searchResults.size)
        assertEquals("Test Result", searchResults[0].title)
        assertEquals(0.95, searchResults[0].relevanceScore)
    }
    
    @Test
    fun `search returns empty list when no results found`() = runTest {
        val dto = SearchResponseDto(
            results = emptyList(),
            total = 0,
            page = 1
        )
        coEvery { searchService.search("test", 1, 10) } returns Response.success(dto)
        
        val result = dataSource.search("test", 1, 10)
        
        assertTrue(result is Result.Success)
        val searchResults = (result as Result.Success).data
        assertTrue(searchResults.isEmpty())
    }
}
