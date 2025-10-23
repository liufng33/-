package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.repository.SourceRepository

class AddSourceUseCase(private val repository: SourceRepository) {
    suspend operator fun invoke(source: Source): Result<Unit> {
        return repository.validateSource(source).fold(
            onSuccess = { repository.addSource(source) },
            onFailure = { Result.failure(it) }
        )
    }
}
