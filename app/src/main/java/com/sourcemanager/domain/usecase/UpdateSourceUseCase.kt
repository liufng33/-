package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.repository.SourceRepository

class UpdateSourceUseCase(private val repository: SourceRepository) {
    suspend operator fun invoke(source: Source): Result<Unit> {
        return repository.validateSource(source).fold(
            onSuccess = { repository.updateSource(source) },
            onFailure = { Result.failure(it) }
        )
    }
}
