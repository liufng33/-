package com.sourcemanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.usecase.DeleteSourceUseCase
import com.sourcemanager.domain.usecase.GetSourcesUseCase
import com.sourcemanager.domain.usecase.SwitchActiveSourceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class SourceListViewModelTest {

    private lateinit var viewModel: SourceListViewModel
    private lateinit var getSourcesUseCase: GetSourcesUseCase
    private lateinit var deleteSourceUseCase: DeleteSourceUseCase
    private lateinit var switchActiveSourceUseCase: SwitchActiveSourceUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSourcesUseCase = mockk()
        deleteSourceUseCase = mockk()
        switchActiveSourceUseCase = mockk()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty with loading false`() = runTest {
        val sources = emptyList<Source>()
        every { getSourcesUseCase() } returns flowOf(sources)

        viewModel = SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(sources, state.sources)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `should load sources on initialization`() = runTest {
        val sources = listOf(
            Source("1", "Source 1", SourceType.SEARCH, "https://example.com/1"),
            Source("2", "Source 2", SourceType.PARSER, "https://example.com/2")
        )
        every { getSourcesUseCase() } returns flowOf(sources)

        viewModel = SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.sources.size)
            assertEquals("Source 1", state.sources[0].name)
        }
    }

    @Test
    fun `deleteSource should call use case and show success message`() = runTest {
        val sources = listOf(
            Source("1", "Source 1", SourceType.SEARCH, "https://example.com/1")
        )
        every { getSourcesUseCase() } returns flowOf(sources)
        coEvery { deleteSourceUseCase("1") } returns Result.success(Unit)

        viewModel = SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteSource("1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { deleteSourceUseCase("1") }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Source deleted successfully", state.successMessage)
        }
    }

    @Test
    fun `deleteSource should show error message on failure`() = runTest {
        val sources = listOf(
            Source("1", "Source 1", SourceType.SEARCH, "https://example.com/1")
        )
        every { getSourcesUseCase() } returns flowOf(sources)
        coEvery { deleteSourceUseCase("1") } returns Result.failure(Exception("Delete failed"))

        viewModel = SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteSource("1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Delete failed", state.error)
        }
    }

    @Test
    fun `switchActiveSource should call use case successfully`() = runTest {
        val sources = listOf(
            Source("1", "Source 1", SourceType.SEARCH, "https://example.com/1")
        )
        every { getSourcesUseCase() } returns flowOf(sources)
        coEvery { switchActiveSourceUseCase("1", SourceType.SEARCH) } returns Result.success(Unit)

        viewModel = SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.switchActiveSource("1", SourceType.SEARCH)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { switchActiveSourceUseCase("1", SourceType.SEARCH) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Active source switched successfully", state.successMessage)
        }
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        val sources = emptyList<Source>()
        every { getSourcesUseCase() } returns flowOf(sources)
        coEvery { deleteSourceUseCase("1") } returns Result.failure(Exception("Error"))

        viewModel = SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteSource("1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
        }
    }
}
