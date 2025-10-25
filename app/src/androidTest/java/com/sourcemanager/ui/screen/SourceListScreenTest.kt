package com.sourcemanager.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.ui.theme.SourceManagerTheme
import com.sourcemanager.ui.viewmodel.SourceListUiState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SourceListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun sourceListScreen_displaysEmptyState_whenNoSources() {
        val uiState = SourceListUiState(sources = emptyList())

        composeTestRule.setContent {
            SourceManagerTheme {
                SourceListScreen(
                    uiState = uiState,
                    onAddClick = {},
                    onEditClick = {},
                    onImportClick = {},
                    onDeleteSource = {},
                    onSwitchActiveSource = { _, _ -> },
                    onClearError = {},
                    onClearSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No sources available").assertIsDisplayed()
    }

    @Test
    fun sourceListScreen_displaysSources_whenSourcesExist() {
        val sources = listOf(
            Source(
                id = "1",
                name = "Test Source",
                type = SourceType.SEARCH,
                url = "https://example.com",
                description = "Test description"
            )
        )
        val uiState = SourceListUiState(sources = sources)

        composeTestRule.setContent {
            SourceManagerTheme {
                SourceListScreen(
                    uiState = uiState,
                    onAddClick = {},
                    onEditClick = {},
                    onImportClick = {},
                    onDeleteSource = {},
                    onSwitchActiveSource = { _, _ -> },
                    onClearError = {},
                    onClearSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Source").assertIsDisplayed()
        composeTestRule.onNodeWithText("https://example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test description").assertIsDisplayed()
    }

    @Test
    fun sourceListScreen_displaysLoadingIndicator_whenLoading() {
        val uiState = SourceListUiState(isLoading = true)

        composeTestRule.setContent {
            SourceManagerTheme {
                SourceListScreen(
                    uiState = uiState,
                    onAddClick = {},
                    onEditClick = {},
                    onImportClick = {},
                    onDeleteSource = {},
                    onSwitchActiveSource = { _, _ -> },
                    onClearError = {},
                    onClearSuccess = {}
                )
            }
        }

        composeTestRule.onNode(hasTestTag("progress")).assertDoesNotExist()
    }

    @Test
    fun sourceListScreen_clickAddButton_triggersCallback() {
        val uiState = SourceListUiState()
        var addClicked = false

        composeTestRule.setContent {
            SourceManagerTheme {
                SourceListScreen(
                    uiState = uiState,
                    onAddClick = { addClicked = true },
                    onEditClick = {},
                    onImportClick = {},
                    onDeleteSource = {},
                    onSwitchActiveSource = { _, _ -> },
                    onClearError = {},
                    onClearSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Source").performClick()
        assert(addClicked)
    }

    @Test
    fun sourceListScreen_clickImportButton_triggersCallback() {
        val uiState = SourceListUiState()
        var importClicked = false

        composeTestRule.setContent {
            SourceManagerTheme {
                SourceListScreen(
                    uiState = uiState,
                    onAddClick = {},
                    onEditClick = {},
                    onImportClick = { importClicked = true },
                    onDeleteSource = {},
                    onSwitchActiveSource = { _, _ -> },
                    onClearError = {},
                    onClearSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Import").performClick()
        assert(importClicked)
    }
}
