package com.sourcemanager.di

import com.sourcemanager.data.repository.SourceRepositoryImpl
import com.sourcemanager.domain.repository.SourceRepository
import com.sourcemanager.domain.usecase.*

class AppContainer {
    val sourceRepository: SourceRepository = SourceRepositoryImpl()

    val getSourcesUseCase = GetSourcesUseCase(sourceRepository)
    val addSourceUseCase = AddSourceUseCase(sourceRepository)
    val updateSourceUseCase = UpdateSourceUseCase(sourceRepository)
    val deleteSourceUseCase = DeleteSourceUseCase(sourceRepository)
    val switchActiveSourceUseCase = SwitchActiveSourceUseCase(sourceRepository)
    val importSourcesFromApiUseCase = ImportSourcesFromApiUseCase(sourceRepository)
    val importSourcesFromJsonUseCase = ImportSourcesFromJsonUseCase(sourceRepository)
}
