package com.example.videoplayer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.model.StreamQuality
import com.example.videoplayer.data.parser.DirectUrlParser
import com.example.videoplayer.data.parser.JsoupParser
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import com.example.videoplayer.ui.url.UrlInputScreen
import com.example.videoplayer.ui.url.UrlInputViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UrlInputScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun urlInputScreen_displaysTitleAndInputField() {
        val jsoupParser = mockk<JsoupParser>(relaxed = true)
        val directUrlParser = mockk<DirectUrlParser>(relaxed = true)
        val viewModel = UrlInputViewModel(jsoupParser, directUrlParser)

        composeTestRule.setContent {
            VideoPlayerTheme {
                UrlInputScreen(
                    onNavigateToPlayer = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Enter Video URL").assertIsDisplayed()
        composeTestRule.onNodeWithText("Video URL").assertIsDisplayed()
        composeTestRule.onNodeWithText("Parse URL").assertIsDisplayed()
    }

    @Test
    fun urlInputScreen_showsParserSourceDropdown() {
        val jsoupParser = mockk<JsoupParser>(relaxed = true)
        val directUrlParser = mockk<DirectUrlParser>(relaxed = true)
        val viewModel = UrlInputViewModel(jsoupParser, directUrlParser)

        composeTestRule.setContent {
            VideoPlayerTheme {
                UrlInputScreen(
                    onNavigateToPlayer = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Parser Source").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jsoup HTML Parser").assertIsDisplayed()
    }

    @Test
    fun urlInputScreen_canEnterUrl() {
        val jsoupParser = mockk<JsoupParser>(relaxed = true)
        val directUrlParser = mockk<DirectUrlParser>(relaxed = true)
        val viewModel = UrlInputViewModel(jsoupParser, directUrlParser)

        composeTestRule.setContent {
            VideoPlayerTheme {
                UrlInputScreen(
                    onNavigateToPlayer = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        val testUrl = "https://example.com/video.mp4"
        composeTestRule.onNodeWithText("Video URL")
            .performTextInput(testUrl)

        composeTestRule.onNodeWithText(testUrl).assertExists()
    }

    @Test
    fun urlInputScreen_parseButtonIsDisabledWhenUrlIsEmpty() {
        val jsoupParser = mockk<JsoupParser>(relaxed = true)
        val directUrlParser = mockk<DirectUrlParser>(relaxed = true)
        val viewModel = UrlInputViewModel(jsoupParser, directUrlParser)

        composeTestRule.setContent {
            VideoPlayerTheme {
                UrlInputScreen(
                    onNavigateToPlayer = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Parse URL").assertIsNotEnabled()
    }

    @Test
    fun urlInputScreen_parseButtonIsEnabledWhenUrlIsNotEmpty() {
        val jsoupParser = mockk<JsoupParser>(relaxed = true)
        val directUrlParser = mockk<DirectUrlParser>(relaxed = true)
        val viewModel = UrlInputViewModel(jsoupParser, directUrlParser)

        composeTestRule.setContent {
            VideoPlayerTheme {
                UrlInputScreen(
                    onNavigateToPlayer = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Video URL")
            .performTextInput("https://example.com/video.mp4")

        composeTestRule.onNodeWithText("Parse URL").assertIsEnabled()
    }
}
