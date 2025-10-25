package com.remotedata.data.remote.datasource

import com.remotedata.data.remote.api.ApiService
import com.remotedata.data.remote.dto.ApiResponseDto
import com.remotedata.utils.NoOpRateLimiter
import com.remotedata.utils.RemoteException
import com.remotedata.utils.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class ApiRemoteDataSourceTest {
    
    private lateinit var apiService: ApiService
    private lateinit var dataSource: ApiRemoteDataSource
    
    @BeforeEach
    fun setup() {
        apiService = mockk()
        dataSource = ApiRemoteDataSourceImpl(apiService, NoOpRateLimiter())
    }
    
    @Test
    fun `fetchApiData returns success when API returns valid response`() = runTest {
        val dto = ApiResponseDto(
            id = "3",
            status = 200,
            data = "test data",
            timestamp = 123456789L
        )
        coEvery { apiService.getApiData("3") } returns Response.success(dto)
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Success)
        val apiResult = (result as Result.Success).data
        assertEquals("3", apiResult.id)
        assertEquals(200, apiResult.status)
        assertEquals("test data", apiResult.data)
    }
    
    @Test
    fun `fetchApiData returns error when API returns null body`() = runTest {
        coEvery { apiService.getApiData("3") } returns Response.success(null)
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is RemoteException.ParseError)
    }
    
    @Test
    fun `fetchApiData returns error when API returns 404`() = runTest {
        coEvery { apiService.getApiData("3") } returns Response.error(
            404,
            "Not Found".toResponseBody()
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is RemoteException.HttpError)
        assertEquals(404, (exception as RemoteException.HttpError).code)
    }
    
    @Test
    fun `fetchApiData returns rate limit error when API returns 429`() = runTest {
        coEvery { apiService.getApiData("3") } returns Response.error(
            429,
            "Too Many Requests".toResponseBody()
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is RemoteException.RateLimitError)
    }
    
    @Test
    fun `fetchRawData returns success when API returns string`() = runTest {
        coEvery { apiService.getApiDataAsString("3") } returns Response.success("raw response")
        
        val result = dataSource.fetchRawData("3")
        
        assertTrue(result is Result.Success)
        assertEquals("raw response", (result as Result.Success).data)
    }
    
    @Test
    fun `fetchApiData returns server error when API returns 500`() = runTest {
        coEvery { apiService.getApiData("3") } returns Response.error(
            500,
            "Internal Server Error".toResponseBody()
        )
        
        val result = dataSource.fetchApiData("3")
        
        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is RemoteException.HttpError)
        assertEquals(500, (exception as RemoteException.HttpError).code)
    }
}
