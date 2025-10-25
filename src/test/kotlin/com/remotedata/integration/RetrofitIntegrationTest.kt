package com.remotedata.integration

import com.remotedata.data.remote.api.ApiService
import com.remotedata.data.remote.api.RetrofitConfig
import com.remotedata.data.remote.datasource.ApiRemoteDataSource
import com.remotedata.data.remote.datasource.ApiRemoteDataSourceImpl
import com.remotedata.data.remote.dto.ApiResponseDto
import com.remotedata.utils.NoOpRateLimiter
import com.remotedata.utils.Result
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RetrofitIntegrationTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var dataSource: ApiRemoteDataSource
    
    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val gson = RetrofitConfig.provideGson()
        val okHttpClient = RetrofitConfig.provideOkHttpClient(enableLogging = false, timeoutSeconds = 5)
        val retrofit = RetrofitConfig.provideRetrofit(
            baseUrl = mockWebServer.url("/").toString(),
            okHttpClient = okHttpClient,
            gson = gson
        )
        
        apiService = RetrofitConfig.createService(retrofit)
        dataSource = ApiRemoteDataSourceImpl(apiService, NoOpRateLimiter())
    }
    
    @AfterEach
    fun teardown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `fetchApiData successfully parses JSON response`() = runTest {
        val jsonResponse = """
            {
                "id": "3",
                "status": 200,
                "data": "test data",
                "timestamp": 123456789
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .setHeader("Content-Type", "application/json")
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Success)
        val apiResult = (result as Result.Success).data
        assertEquals("3", apiResult.id)
        assertEquals(200, apiResult.status)
        assertEquals("test data", apiResult.data)
    }
    
    @Test
    fun `fetchApiData handles 404 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )
        
        val result = dataSource.fetchApiData("999")
        
        assertTrue(result is Result.Error)
    }
    
    @Test
    fun `fetchApiData handles rate limiting`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setHeader("Retry-After", "60")
                .setBody("Too Many Requests")
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
    }
    
    @Test
    fun `fetchRawData returns plain text response`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("plain text response")
                .setHeader("Content-Type", "text/plain")
        )
        
        val result = dataSource.fetchRawData("3")
        
        assertTrue(result is Result.Success)
        assertEquals("plain text response", (result as Result.Success).data)
    }
    
    @Test
    fun `fetchApiData handles malformed JSON`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("not valid json")
                .setHeader("Content-Type", "application/json")
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
    }
    
    @Test
    fun `fetchApiData handles server error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
    }
    
    @Test
    fun `verifies request is made with correct query parameter`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .setHeader("Content-Type", "application/json")
        )
        
        dataSource.fetchApiData("test-id")
        
        val request = mockWebServer.takeRequest()
        assertTrue(request.path?.contains("id=test-id") == true)
    }
}
