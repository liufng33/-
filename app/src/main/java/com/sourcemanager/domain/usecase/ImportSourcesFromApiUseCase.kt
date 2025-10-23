package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow

class ImportSourcesFromApiUseCase(private val repository: SourceRepository) {
    suspend operator fun invoke(apiUrl: String): Flow<ImportResult> {
        return repository.importFromApi(apiUrl)
    }
}
