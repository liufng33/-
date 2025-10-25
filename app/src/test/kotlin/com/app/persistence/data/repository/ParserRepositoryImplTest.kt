package com.app.persistence.data.repository

import com.app.persistence.data.cache.CacheManager
import com.app.persistence.data.local.database.dao.CustomRuleDao
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.local.database.entity.CustomRuleEntity
import com.app.persistence.data.local.database.entity.SourceEntity
import com.app.persistence.data.remote.dto.VideoDto
import com.app.persistence.data.remote.source.ParserRemoteDataSource
import com.app.persistence.domain.model.ParserConfig
import com.app.persistence.domain.model.Result
import com.app.persistence.domain.model.SourceType
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ParserRepositoryImplTest {
    
    @MockK
    private lateinit var sourceDao: SourceDao
    
    @MockK
    private lateinit var customRuleDao: CustomRuleDao
    
    @MockK
    private lateinit var parserRemoteDataSource: ParserRemoteDataSource
    
    @MockK
    private lateinit var cacheManager: CacheManager
    
    private lateinit var repository: ParserRepositoryImpl
    
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = ParserRepositoryImpl(sourceDao, customRuleDao, parserRemoteDataSource, cacheManager)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `findParserForUrl returns cached parser when available`() = runTest {
        // Given
        val url = "https://example.com/video/123"
        val cachedParser = ParserConfig(
            id = "1",
            name = "Test Parser",
            urlPattern = ".*example\\.com.*"
        )
        
        coEvery { cacheManager.get<ParserConfig>(any()) } returns cachedParser
        
        // When
        val result = repository.findParserForUrl(url)
        
        // Then
        assertEquals(cachedParser, result)
        coVerify(exactly = 0) { sourceDao.getSourcesByType(any()) }
    }
    
    @Test
    fun `findParserForUrl finds matching parser from database`() = runTest {
        // Given
        val url = "https://example.com/video/123"
        val sourceEntity = SourceEntity(
            id = 1,
            name = "Test Parser",
            type = SourceType.PARSER.name,
            baseUrl = "https://example.com",
            parserClass = ".*example\\.com.*",
            isEnabled = true,
            priority = 1
        )
        val ruleEntity = CustomRuleEntity(
            id = 1,
            sourceId = 1,
            name = "Title Rule",
            ruleType = "REGEX",
            pattern = "<title>(.*?)</title>",
            replacement = "$1",
            isEnabled = true,
            priority = 1
        )
        
        coEvery { cacheManager.get<ParserConfig>(any()) } returns null
        every { sourceDao.getSourcesByType(SourceType.PARSER.name) } returns flowOf(listOf(sourceEntity))
        coEvery { customRuleDao.getRulesBySourceId(1) } returns listOf(ruleEntity)
        coEvery { cacheManager.put(any(), any<ParserConfig>(), any()) } just Runs
        
        // When
        val result = repository.findParserForUrl(url)
        
        // Then
        assertNotNull(result)
        assertEquals("Test Parser", result.name)
        assertEquals(1, result.rules.size)
        coVerify { cacheManager.put(any(), any<ParserConfig>(), any()) }
    }
    
    @Test
    fun `findParserForUrl returns null when no parser matches`() = runTest {
        // Given
        val url = "https://nomatch.com/video/123"
        val sourceEntity = SourceEntity(
            id = 1,
            name = "Test Parser",
            type = SourceType.PARSER.name,
            baseUrl = "https://example.com",
            parserClass = ".*example\\.com.*",
            isEnabled = true,
            priority = 1
        )
        
        coEvery { cacheManager.get<ParserConfig>(any()) } returns null
        every { sourceDao.getSourcesByType(SourceType.PARSER.name) } returns flowOf(listOf(sourceEntity))
        coEvery { customRuleDao.getRulesBySourceId(1) } returns emptyList()
        
        // When
        val result = repository.findParserForUrl(url)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `getAllParsers returns all parser configs`() = runTest {
        // Given
        val sourceEntity1 = SourceEntity(
            id = 1,
            name = "Parser 1",
            type = SourceType.PARSER.name,
            baseUrl = "https://example1.com",
            parserClass = ".*example1\\.com.*",
            isEnabled = true,
            priority = 1
        )
        val sourceEntity2 = sourceEntity1.copy(id = 2, name = "Parser 2")
        
        coEvery { cacheManager.get<List<ParserConfig>>(any()) } returns null
        every { sourceDao.getSourcesByType(SourceType.PARSER.name) } returns flowOf(listOf(sourceEntity1, sourceEntity2))
        coEvery { customRuleDao.getRulesBySourceId(any()) } returns emptyList()
        coEvery { cacheManager.put(any(), any<List<ParserConfig>>(), any()) } just Runs
        
        // When
        val result = repository.getAllParsers()
        
        // Then
        assertEquals(2, result.size)
        assertEquals("Parser 1", result[0].name)
        assertEquals("Parser 2", result[1].name)
    }
    
    @Test
    fun `getActiveParsers returns only enabled parsers`() = runTest {
        // Given
        val enabledParser = ParserConfig(id = "1", name = "Enabled", urlPattern = ".*", enabled = true)
        val disabledParser = ParserConfig(id = "2", name = "Disabled", urlPattern = ".*", enabled = false)
        val allParsers = listOf(enabledParser, disabledParser)
        
        coEvery { cacheManager.get<List<ParserConfig>>(any()) } returns allParsers
        
        // When
        val result = repository.getActiveParsers()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Enabled", result[0].name)
    }
    
    @Test
    fun `parseVideoPage returns cached result when available`() = runTest {
        // Given
        val parser = ParserConfig(id = "1", name = "Test Parser", urlPattern = ".*")
        val url = "https://example.com/video/123"
        val cachedVideo = com.app.persistence.domain.model.VideoItem(
            id = "123",
            title = "Cached Video",
            url = url,
            sourceId = "1"
        )
        
        coEvery { cacheManager.get<com.app.persistence.domain.model.VideoItem>(any()) } returns cachedVideo
        
        // When
        val result = repository.parseVideoPage(parser, url)
        
        // Then
        assertEquals(cachedVideo, result)
        coVerify(exactly = 0) { parserRemoteDataSource.parseVideoPage(any(), any()) }
    }
    
    @Test
    fun `parseVideoPage fetches from remote when cache miss`() = runTest {
        // Given
        val parser = ParserConfig(id = "1", name = "Test Parser", urlPattern = ".*")
        val url = "https://example.com/video/123"
        val videoDto = VideoDto(
            id = "123",
            title = "Remote Video",
            url = url,
            sourceId = "1"
        )
        
        coEvery { cacheManager.get<com.app.persistence.domain.model.VideoItem>(any()) } returns null
        coEvery { parserRemoteDataSource.parseVideoPage(any(), any()) } returns Result.Success(videoDto)
        coEvery { cacheManager.put(any(), any<com.app.persistence.domain.model.VideoItem>(), any()) } just Runs
        
        // When
        val result = repository.parseVideoPage(parser, url)
        
        // Then
        assertNotNull(result)
        assertEquals("Remote Video", result.title)
        coVerify { cacheManager.put(any(), any<com.app.persistence.domain.model.VideoItem>(), any()) }
    }
    
    @Test
    fun `parseVideoPage returns null on parse error`() = runTest {
        // Given
        val parser = ParserConfig(id = "1", name = "Test Parser", urlPattern = ".*")
        val url = "https://example.com/video/123"
        
        coEvery { cacheManager.get<com.app.persistence.domain.model.VideoItem>(any()) } returns null
        coEvery { parserRemoteDataSource.parseVideoPage(any(), any()) } returns 
            Result.Error(com.app.persistence.domain.model.DataError.ParseError("Parse failed"))
        
        // When
        val result = repository.parseVideoPage(parser, url)
        
        // Then
        assertNull(result)
    }
}
