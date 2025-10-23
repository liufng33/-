package com.cleanarch.domain.usecase

import com.cleanarch.domain.model.Result
import com.cleanarch.domain.repository.SampleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSampleDataUseCaseTest {
    
    private lateinit var repository: SampleRepository
    private lateinit var useCase: GetSampleDataUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSampleDataUseCase(repository)
    }
    
    @Test
    fun `invoke returns success when repository returns success`() = runTest {
        val expectedData = "test data"
        coEvery { repository.fetchData() } returns Result.Success(expectedData)
        
        val result = useCase()
        
        assertTrue(result is Result.Success)
        assertEquals(expectedData, (result as Result.Success).data)
    }
    
    @Test
    fun `invoke returns error when repository returns error`() = runTest {
        val exception = Exception("Test error")
        coEvery { repository.fetchData() } returns Result.Error(exception)
        
        val result = useCase()
        
        assertTrue(result is Result.Error)
    }
}
