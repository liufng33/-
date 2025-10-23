package com.sourcemanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
    navController: NavHostController,
    sourceListViewModel: SourceListViewModel,
    addEditSourceViewModelFactory: (String?) -> AddEditSourceViewModel,
    importSourceViewModel: ImportSourceViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SourceList.route
    ) {
        composable(Screen.SourceList.route) {
            val uiState by sourceListViewModel.uiState.collectAsState()
            
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
                    sourceListViewModel.deleteSource(sourceId)
                },
                onSwitchActiveSource = { sourceId, type ->
                    sourceListViewModel.switchActiveSource(sourceId, type)
                },
                onClearError = { sourceListViewModel.clearError() },
                onClearSuccess = { sourceListViewModel.clearSuccessMessage() }
            )
        }

        composable(Screen.AddSource.route) {
            val viewModel = addEditSourceViewModelFactory(null)
            val uiState by viewModel.uiState.collectAsState()

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
            val viewModel = addEditSourceViewModelFactory(sourceId)
            val uiState by viewModel.uiState.collectAsState()

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
            val uiState by importSourceViewModel.uiState.collectAsState()

            ImportSourceScreen(
                uiState = uiState,
                onApiUrlChange = { importSourceViewModel.updateApiUrl(it) },
                onJsonContentChange = { importSourceViewModel.updateJsonContent(it) },
                onImportFromApi = { importSourceViewModel.importFromApi() },
                onImportFromJson = { importSourceViewModel.importFromJson() },
                onNavigateBack = { navController.popBackStack() },
                onClearError = { importSourceViewModel.clearError() },
                onClearSuccess = { importSourceViewModel.clearSuccessMessage() }
            )
        }
    }
}
