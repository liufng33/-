package com.cleanarch.app.ui.screens.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cleanarch.domain.model.Result
import com.cleanarch.domain.usecase.GetSampleDataUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var getSampleDataUseCase: GetSampleDataUseCase
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSampleDataUseCase = mockk()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `viewModel loads data successfully on init`() {
        val expectedData = "test data"
        coEvery { getSampleDataUseCase() } returns Result.Success(expectedData)
        
        viewModel = HomeViewModel(getSampleDataUseCase)
        
        assertEquals(expectedData, viewModel.currentState.data)
    }
}
