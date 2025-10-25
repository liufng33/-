package com.remotedata.integration

import com.remotedata.data.remote.datasource.*
import com.remotedata.data.remote.parser.HtmlFetcher
import com.remotedata.data.remote.parser.HtmlParser
import com.remotedata.di.appModules
import com.remotedata.utils.RateLimiter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.inject

class KoinModuleTest {
    
    @BeforeEach
    fun setup() {
        startKoin {
            modules(appModules)
        }
    }
    
    @AfterEach
    fun teardown() {
        stopKoin()
    }
    
    @Test
    fun `verify RateLimiter is provided`() {
        val rateLimiter by inject<RateLimiter>(RateLimiter::class.java)
        assertNotNull(rateLimiter)
    }
    
    @Test
    fun `verify HtmlParser is provided`() {
        val htmlParser by inject<HtmlParser>(HtmlParser::class.java)
        assertNotNull(htmlParser)
    }
    
    @Test
    fun `verify HtmlFetcher is provided`() {
        val htmlFetcher by inject<HtmlFetcher>(HtmlFetcher::class.java)
        assertNotNull(htmlFetcher)
    }
    
    @Test
    fun `verify ApiRemoteDataSource is provided`() {
        val dataSource by inject<ApiRemoteDataSource>(ApiRemoteDataSource::class.java)
        assertNotNull(dataSource)
    }
    
    @Test
    fun `verify SearchRemoteDataSource is provided`() {
        val dataSource by inject<SearchRemoteDataSource>(SearchRemoteDataSource::class.java)
        assertNotNull(dataSource)
    }
    
    @Test
    fun `verify ParserRemoteDataSource is provided`() {
        val dataSource by inject<ParserRemoteDataSource>(ParserRemoteDataSource::class.java)
        assertNotNull(dataSource)
    }
    
    @Test
    fun `verify DynamicApiDataSource is provided`() {
        val dataSource by inject<DynamicApiDataSource>(DynamicApiDataSource::class.java)
        assertNotNull(dataSource)
    }
    
    @Test
    fun `verify all modules can be loaded without errors`() {
        val rateLimiter by inject<RateLimiter>(RateLimiter::class.java)
        val htmlParser by inject<HtmlParser>(HtmlParser::class.java)
        val apiDataSource by inject<ApiRemoteDataSource>(ApiRemoteDataSource::class.java)
        val searchDataSource by inject<SearchRemoteDataSource>(SearchRemoteDataSource::class.java)
        val parserDataSource by inject<ParserRemoteDataSource>(ParserRemoteDataSource::class.java)
        val dynamicDataSource by inject<DynamicApiDataSource>(DynamicApiDataSource::class.java)
        
        assertNotNull(rateLimiter)
        assertNotNull(htmlParser)
        assertNotNull(apiDataSource)
        assertNotNull(searchDataSource)
        assertNotNull(parserDataSource)
        assertNotNull(dynamicDataSource)
    }
}
