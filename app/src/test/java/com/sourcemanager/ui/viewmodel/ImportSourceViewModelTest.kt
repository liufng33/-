package com.sourcemanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.usecase.ImportSourcesFromApiUseCase
import com.sourcemanager.domain.usecase.ImportSourcesFromJsonUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ImportSourceViewModelTest {

    private lateinit var viewModel: ImportSourceViewModel
    private lateinit var importFromApiUseCase: ImportSourcesFromApiUseCase
    private lateinit var importFromJsonUseCase: ImportSourcesFromJsonUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        importFromApiUseCase = mockk()
        importFromJsonUseCase = mockk()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.apiUrl.isEmpty())
            assertTrue(state.jsonContent.isEmpty())
            assertFalse(state.isLoading)
            assertEquals(0f, state.progress)
        }
    }

    @Test
    fun `updateApiUrl should update url in state`() = runTest {
        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.updateApiUrl("https://api.example.com")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("https://api.example.com", state.apiUrl)
        }
    }

    @Test
    fun `importFromApi should fail validation when url is empty`() = runTest {
        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.importFromApi()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("API URL is required", state.apiUrlError)
        }
    }

    @Test
    fun `importFromApi should handle progress and success`() = runTest {
        val importFlow = flowOf(
            ImportResult.Progress(1, 2),
            ImportResult.Success(2)
        )
        coEvery { importFromApiUseCase("https://api.example.com") } returns importFlow

        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.updateApiUrl("https://api.example.com")
        viewModel.importFromApi()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { importFromApiUseCase("https://api.example.com") }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1f, state.progress)
            assertEquals("Successfully imported 2 sources", state.successMessage)
            assertEquals(2, state.importedCount)
        }
    }

    @Test
    fun `importFromApi should handle error`() = runTest {
        val importFlow = flowOf(
            ImportResult.Error("Import failed")
        )
        coEvery { importFromApiUseCase("https://api.example.com") } returns importFlow

        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.updateApiUrl("https://api.example.com")
        viewModel.importFromApi()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Import failed", state.error)
            assertEquals(0f, state.progress)
        }
    }

    @Test
    fun `importFromJson should fail validation when content is empty`() = runTest {
        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.importFromJson()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("JSON content is required", state.error)
        }
    }

    @Test
    fun `importFromJson should handle progress and success`() = runTest {
        val jsonContent = """[{"id":"1","name":"Test","type":"SEARCH","url":"https://example.com"}]"""
        val importFlow = flowOf(
            ImportResult.Progress(1, 1),
            ImportResult.Success(1)
        )
        coEvery { importFromJsonUseCase(jsonContent) } returns importFlow

        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.updateJsonContent(jsonContent)
        viewModel.importFromJson()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { importFromJsonUseCase(jsonContent) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1f, state.progress)
            assertEquals("Successfully imported 1 sources", state.successMessage)
        }
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.importFromJson()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
        }
    }

    @Test
    fun `clearSuccessMessage should clear success state`() = runTest {
        val importFlow = flowOf(ImportResult.Success(5))
        coEvery { importFromApiUseCase("https://api.example.com") } returns importFlow

        viewModel = ImportSourceViewModel(
            importFromApiUseCase,
            importFromJsonUseCase,
            savedStateHandle
        )

        viewModel.updateApiUrl("https://api.example.com")
        viewModel.importFromApi()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearSuccessMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.successMessage)
        }
    }
}
