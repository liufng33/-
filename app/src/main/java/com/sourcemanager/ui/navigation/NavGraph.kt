package com.sourcemanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sourcemanager.ui.screen.AddEditSourceScreen
import com.sourcemanager.ui.screen.ImportSourceScreen
import com.sourcemanager.ui.screen.SourceListScreen
import com.sourcemanager.ui.viewmodel.AddEditSourceViewModel
import com.sourcemanager.ui.viewmodel.ImportSourceViewModel
import com.sourcemanager.ui.viewmodel.SourceListViewModel

sealed class Screen(val route: String) {
    object SourceList : Screen("source_list")
    object AddSource : Screen("add_source")
    object EditSource : Screen("edit_source/{sourceId}") {
        fun createRoute(sourceId: String) = "edit_source/$sourceId"
    }
    object ImportSource : Screen("import_source")
}

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SourceList.route
    ) {
        composable(Screen.SourceList.route) {
            val viewModel: SourceListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            SourceListScreen(
                uiState = uiState,
                onAddClick = {
                    navController.navigate(Screen.AddSource.route)
                },
                onEditClick = { sourceId ->
                    navController.navigate(Screen.EditSource.createRoute(sourceId))
                },
                onImportClick = {
                    navController.navigate(Screen.ImportSource.route)
                },
                onDeleteSource = { sourceId ->
                    viewModel.deleteSource(sourceId)
                },
                onSwitchActiveSource = { sourceId, type ->
                    viewModel.switchActiveSource(sourceId, type)
                },
                onClearError = { viewModel.clearError() },
                onClearSuccess = { viewModel.clearSuccessMessage() }
            )
        }

        composable(Screen.AddSource.route) {
            val viewModel: AddEditSourceViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.popBackStack()
                }
            }

            AddEditSourceScreen(
                uiState = uiState,
                isEdit = false,
                onNameChange = { viewModel.updateName(it) },
                onUrlChange = { viewModel.updateUrl(it) },
                onDescriptionChange = { viewModel.updateDescription(it) },
                onSourceTypeChange = { viewModel.updateSourceType(it) },
                onSaveClick = { viewModel.saveSource(isEdit = false) },
                onNavigateBack = { navController.popBackStack() },
                onClearError = { viewModel.clearError() }
            )
        }

        composable(
            route = Screen.EditSource.route,
            arguments = listOf(
                navArgument("sourceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sourceId = backStackEntry.arguments?.getString("sourceId")
            val viewModel: AddEditSourceViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(sourceId) {
                sourceId?.let {
                    viewModel.loadSourceById(it)
                }
            }

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.popBackStack()
                }
            }

            AddEditSourceScreen(
                uiState = uiState,
                isEdit = true,
                onNameChange = { viewModel.updateName(it) },
                onUrlChange = { viewModel.updateUrl(it) },
                onDescriptionChange = { viewModel.updateDescription(it) },
                onSourceTypeChange = { viewModel.updateSourceType(it) },
                onSaveClick = { viewModel.saveSource(isEdit = true) },
                onNavigateBack = { navController.popBackStack() },
                onClearError = { viewModel.clearError() }
            )
        }

        composable(Screen.ImportSource.route) {
            val viewModel: ImportSourceViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            ImportSourceScreen(
                uiState = uiState,
                onApiUrlChange = { viewModel.updateApiUrl(it) },
                onJsonContentChange = { viewModel.updateJsonContent(it) },
                onImportFromApi = { viewModel.importFromApi() },
                onImportFromJson = { viewModel.importFromJson() },
                onNavigateBack = { navController.popBackStack() },
                onClearError = { viewModel.clearError() },
                onClearSuccess = { viewModel.clearSuccessMessage() }
            )
        }
    }
}
