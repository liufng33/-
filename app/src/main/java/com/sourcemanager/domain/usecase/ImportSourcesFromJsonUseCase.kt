package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImportSourcesFromJsonUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    suspend operator fun invoke(jsonContent: String): Flow<ImportResult> {
        return repository.importFromJson(jsonContent)
    }
}
