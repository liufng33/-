package com.sourcemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sourcemanager.domain.usecase.*
import com.sourcemanager.ui.navigation.NavGraph
import com.sourcemanager.ui.theme.SourceManagerTheme
import com.sourcemanager.ui.viewmodel.AddEditSourceViewModel
import com.sourcemanager.ui.viewmodel.ImportSourceViewModel
import com.sourcemanager.ui.viewmodel.SourceListViewModel
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appContainer = (application as SourceManagerApplication).appContainer

        setContent {
            SourceManagerTheme {
                SourceManagerApp(appContainer)
            }
        }
    }
}

@Composable
fun SourceManagerApp(appContainer: AppContainer) {
    val navController = rememberNavController()

    val sourceListViewModel: SourceListViewModel = viewModel(
        factory = SourceListViewModelFactory(
            appContainer.getSourcesUseCase,
            appContainer.deleteSourceUseCase,
            appContainer.switchActiveSourceUseCase
        )
    )

    val importSourceViewModel: ImportSourceViewModel = viewModel(
        factory = ImportSourceViewModelFactory(
            appContainer.importSourcesFromApiUseCase,
            appContainer.importSourcesFromJsonUseCase
        )
    )

    NavGraph(
        navController = navController,
        sourceListViewModel = sourceListViewModel,
        addEditSourceViewModelFactory = { sourceId ->
            val factory = AddEditSourceViewModelFactory(
                appContainer.addSourceUseCase,
                appContainer.updateSourceUseCase,
                appContainer.sourceRepository,
                sourceId
            )
            factory.create(AddEditSourceViewModel::class.java)
        },
        importSourceViewModel = importSourceViewModel
    )
}

class SourceListViewModelFactory(
    private val getSourcesUseCase: GetSourcesUseCase,
    private val deleteSourceUseCase: DeleteSourceUseCase,
    private val switchActiveSourceUseCase: SwitchActiveSourceUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SourceListViewModel(
            getSourcesUseCase,
            deleteSourceUseCase,
            switchActiveSourceUseCase,
            SavedStateHandle()
        ) as T
    }
}

class AddEditSourceViewModelFactory(
    private val addSourceUseCase: AddSourceUseCase,
    private val updateSourceUseCase: UpdateSourceUseCase,
    private val sourceRepository: com.sourcemanager.domain.repository.SourceRepository,
    private val sourceId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = AddEditSourceViewModel(
            addSourceUseCase,
            updateSourceUseCase,
            SavedStateHandle()
        )
        
        sourceId?.let { id ->
            runBlocking {
                sourceRepository.getSourceById(id)?.let { source ->
                    viewModel.loadSource(source)
                }
            }
        }
        
        return viewModel as T
    }
}

class ImportSourceViewModelFactory(
    private val importSourcesFromApiUseCase: ImportSourcesFromApiUseCase,
    private val importSourcesFromJsonUseCase: ImportSourcesFromJsonUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImportSourceViewModel(
            importSourcesFromApiUseCase,
            importSourcesFromJsonUseCase,
            SavedStateHandle()
        ) as T
    }
}
