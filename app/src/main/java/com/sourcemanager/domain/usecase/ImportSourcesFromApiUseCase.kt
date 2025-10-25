package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImportSourcesFromApiUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    suspend operator fun invoke(apiUrl: String): Flow<ImportResult> {
        return repository.importFromApi(apiUrl)
    }
}
