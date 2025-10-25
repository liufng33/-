package com.sourcemanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.usecase.AddSourceUseCase
import com.sourcemanager.domain.usecase.UpdateSourceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditSourceViewModelTest {

    private lateinit var viewModel: AddEditSourceViewModel
    private lateinit var addSourceUseCase: AddSourceUseCase
    private lateinit var updateSourceUseCase: UpdateSourceUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addSourceUseCase = mockk()
        updateSourceUseCase = mockk()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.name.isEmpty())
            assertTrue(state.url.isEmpty())
            assertEquals(SourceType.SEARCH, state.sourceType)
            assertFalse(state.isLoading)
            assertFalse(state.isSaved)
        }
    }

    @Test
    fun `updateName should update name in state`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateName("Test Source")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Test Source", state.name)
        }
    }

    @Test
    fun `updateUrl should update url in state`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateUrl("https://example.com")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("https://example.com", state.url)
        }
    }

    @Test
    fun `updateSourceType should update type in state`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateSourceType(SourceType.PARSER)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(SourceType.PARSER, state.sourceType)
        }
    }

    @Test
    fun `saveSource should fail validation when name is empty`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateUrl("https://example.com")
        viewModel.saveSource(isEdit = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Name is required", state.nameError)
            assertFalse(state.isSaved)
        }
    }

    @Test
    fun `saveSource should fail validation when url is empty`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateName("Test Source")
        viewModel.saveSource(isEdit = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("URL is required", state.urlError)
            assertFalse(state.isSaved)
        }
    }

    @Test
    fun `saveSource should call addSourceUseCase when adding new source`() = runTest {
        coEvery { addSourceUseCase(any()) } returns Result.success(Unit)

        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateName("Test Source")
        viewModel.updateUrl("https://example.com")
        viewModel.saveSource(isEdit = false)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { addSourceUseCase(any()) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSaved)
        }
    }

    @Test
    fun `saveSource should call updateSourceUseCase when editing source`() = runTest {
        coEvery { updateSourceUseCase(any()) } returns Result.success(Unit)

        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateName("Test Source")
        viewModel.updateUrl("https://example.com")
        viewModel.saveSource(isEdit = true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateSourceUseCase(any()) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSaved)
        }
    }

    @Test
    fun `saveSource should show error on failure`() = runTest {
        coEvery { addSourceUseCase(any()) } returns Result.failure(Exception("Save failed"))

        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        viewModel.updateName("Test Source")
        viewModel.updateUrl("https://example.com")
        viewModel.saveSource(isEdit = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Save failed", state.error)
            assertFalse(state.isSaved)
        }
    }

    @Test
    fun `loadSource should populate state with source data`() = runTest {
        viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            savedStateHandle
        )

        val source = Source(
            id = "123",
            name = "Existing Source",
            url = "https://example.com",
            type = SourceType.PARSER,
            description = "Test description"
        )

        viewModel.loadSource(source)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("123", state.id)
            assertEquals("Existing Source", state.name)
            assertEquals("https://example.com", state.url)
            assertEquals(SourceType.PARSER, state.sourceType)
            assertEquals("Test description", state.description)
        }
    }
}
