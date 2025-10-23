package com.example.videoplayer.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.model.StreamQuality
import com.example.videoplayer.data.parser.DirectUrlParser
import com.example.videoplayer.data.parser.JsoupParser
import com.example.videoplayer.data.parser.ParserSource
import com.example.videoplayer.ui.url.UrlInputViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UrlInputViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var jsoupParser: JsoupParser
    private lateinit var directUrlParser: DirectUrlParser
    private lateinit var viewModel: UrlInputViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        jsoupParser = mockk()
        directUrlParser = mockk()
        viewModel = UrlInputViewModel(jsoupParser, directUrlParser)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateUrl updates state correctly`() {
        val testUrl = "https://example.com/video.mp4"
        viewModel.updateUrl(testUrl)
        assertEquals(testUrl, viewModel.uiState.value.url)
    }

    @Test
    fun `selectParserSource updates state correctly`() {
        viewModel.selectParserSource(ParserSource.DIRECT)
        assertEquals(ParserSource.DIRECT, viewModel.uiState.value.selectedParser)
    }

    @Test
    fun `parseUrl with empty URL shows error`() = runTest {
        viewModel.updateUrl("")
        viewModel.parseUrl()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.playbackOptions is PlaybackOptions.Error)
        assertEquals(
            "Please enter a valid URL",
            (state.playbackOptions as PlaybackOptions.Error).message
        )
    }

    @Test
    fun `parseUrl with invalid URL format shows error`() = runTest {
        viewModel.updateUrl("not-a-valid-url")
        viewModel.parseUrl()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.playbackOptions is PlaybackOptions.Error)
        assertEquals(
            "Invalid URL format",
            (state.playbackOptions as PlaybackOptions.Error).message
        )
    }

    @Test
    fun `parseUrl with valid URL and Jsoup parser calls parser`() = runTest {
        val testUrl = "https://example.com/video.html"
        val expectedResult = PlaybackOptions.Success(
            title = "Test Video",
            qualities = listOf(
                StreamQuality(
                    quality = StreamQuality.QUALITY_720P,
                    url = "https://example.com/video.mp4"
                )
            )
        )

        coEvery { jsoupParser.parse(testUrl) } returns expectedResult

        viewModel.updateUrl(testUrl)
        viewModel.selectParserSource(ParserSource.JSOUP)
        viewModel.parseUrl()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.playbackOptions is PlaybackOptions.Success)
        val success = state.playbackOptions as PlaybackOptions.Success
        assertEquals("Test Video", success.title)
        assertEquals(1, success.qualities.size)
    }

    @Test
    fun `parseUrl with direct URL parser calls correct parser`() = runTest {
        val testUrl = "https://example.com/video.mp4"
        val expectedResult = PlaybackOptions.Success(
            title = "video.mp4",
            qualities = listOf(
                StreamQuality(
                    quality = StreamQuality.QUALITY_AUTO,
                    url = testUrl,
                    format = "mp4"
                )
            )
        )

        coEvery { directUrlParser.parse(testUrl) } returns expectedResult

        viewModel.updateUrl(testUrl)
        viewModel.selectParserSource(ParserSource.DIRECT)
        viewModel.parseUrl()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.playbackOptions is PlaybackOptions.Success)
    }

    @Test
    fun `parseUrl shows loading state during parsing`() = runTest {
        val testUrl = "https://example.com/video.html"

        coEvery { jsoupParser.parse(testUrl) } coAnswers {
            kotlinx.coroutines.delay(100)
            PlaybackOptions.Success("Test", emptyList())
        }

        viewModel.updateUrl(testUrl)
        viewModel.parseUrl()

        assertEquals(PlaybackOptions.Loading, viewModel.uiState.value.playbackOptions)

        advanceUntilIdle()
    }

    @Test
    fun `clearResults resets playback options to idle`() {
        viewModel.clearResults()
        assertEquals(PlaybackOptions.Idle, viewModel.uiState.value.playbackOptions)
    }

    @Test
    fun `parseUrl handles parser exception gracefully`() = runTest {
        val testUrl = "https://example.com/video.html"

        coEvery { jsoupParser.parse(testUrl) } throws RuntimeException("Network error")

        viewModel.updateUrl(testUrl)
        viewModel.parseUrl()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.playbackOptions is PlaybackOptions.Error)
        assertTrue((state.playbackOptions as PlaybackOptions.Error).message.contains("Network error"))
    }
}
