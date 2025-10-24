package com.yingshi.video.data.repository

import com.yingshi.video.data.local.dao.CustomRuleDao
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.entity.ParserSourceEntity
import com.yingshi.video.data.local.entity.SearchSourceEntity
import com.yingshi.video.data.remote.ApiService
import com.yingshi.video.domain.model.ParserConfig
import com.yingshi.video.domain.model.SourceConfig
import com.yingshi.video.domain.model.SourceType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SourceRepositoryImplTest {

    private lateinit var searchSourceDao: SearchSourceDao
    private lateinit var parserSourceDao: ParserSourceDao
    private lateinit var customRuleDao: CustomRuleDao
    private lateinit var apiService: ApiService
    private lateinit var repository: SourceRepositoryImpl

    @Before
    fun setup() {
        searchSourceDao = mockk(relaxed = true)
        parserSourceDao = mockk(relaxed = true)
        customRuleDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        
        repository = SourceRepositoryImpl(
            searchSourceDao = searchSourceDao,
            parserSourceDao = parserSourceDao,
            customRuleDao = customRuleDao,
            apiService = apiService
        )
    }

    @Test
    fun `getAllSearchSources returns mapped domain models`() = runTest {
        val entities = listOf(
            SearchSourceEntity(
                id = "1",
                name = "Test Source",
                apiEndpoint = "https://example.com",
                isEnabled = true,
                priority = 0
            )
        )
        
        coEvery { searchSourceDao.getAllSources() } returns flowOf(entities)
        coEvery { customRuleDao.getRulesBySourceId(any()) } returns emptyList()
        
        val result = repository.getAllSearchSources().first()
        
        assert(result.size == 1)
        assert(result[0].name == "Test Source")
        assert(result[0].type == SourceType.SEARCH_SOURCE)
    }

    @Test
    fun `addSearchSource inserts entity to dao`() = runTest {
        val source = SourceConfig(
            id = "1",
            name = "Test Source",
            apiEndpoint = "https://example.com",
            type = SourceType.SEARCH_SOURCE,
            parsingRules = emptyList(),
            isEnabled = true,
            priority = 0
        )
        
        repository.addSearchSource(source)
        
        coVerify { searchSourceDao.insertSource(any()) }
    }

    @Test
    fun `deleteSearchSource deletes by id`() = runTest {
        repository.deleteSearchSource("1")
        
        coVerify { searchSourceDao.deleteSourceById("1") }
    }

    @Test
    fun `getAllParserSources returns mapped domain models`() = runTest {
        val entities = listOf(
            ParserSourceEntity(
                id = "1",
                name = "Test Parser",
                parserUrl = "https://parser.example.com",
                supportedDomains = listOf("example.com"),
                isEnabled = true,
                priority = 0
            )
        )
        
        coEvery { parserSourceDao.getAllParsers() } returns flowOf(entities)
        
        val result = repository.getAllParserSources().first()
        
        assert(result.size == 1)
        assert(result[0].name == "Test Parser")
    }

    @Test
    fun `addParserSource inserts entity to dao`() = runTest {
        val parser = ParserConfig(
            id = "1",
            name = "Test Parser",
            parserUrl = "https://parser.example.com",
            supportedDomains = listOf("example.com"),
            isEnabled = true,
            priority = 0
        )
        
        repository.addParserSource(parser)
        
        coVerify { parserSourceDao.insertParser(any()) }
    }

    @Test
    fun `setSearchSourceEnabled updates enabled status`() = runTest {
        repository.setSearchSourceEnabled("1", false)
        
        coVerify { searchSourceDao.setSourceEnabled("1", false) }
    }

    @Test
    fun `setParserSourceEnabled updates enabled status`() = runTest {
        repository.setParserSourceEnabled("1", false)
        
        coVerify { parserSourceDao.setParserEnabled("1", false) }
    }
}
