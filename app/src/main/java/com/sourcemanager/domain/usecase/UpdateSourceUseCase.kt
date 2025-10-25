package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.repository.SourceRepository
import javax.inject.Inject

class UpdateSourceUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    suspend operator fun invoke(source: Source): Result<Unit> {
        return repository.validateSource(source).fold(
            onSuccess = { repository.updateSource(source) },
            onFailure = { Result.failure(it) }
        )
    }
}
