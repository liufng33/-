package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.repository.SourceRepository

class DeleteSourceUseCase(private val repository: SourceRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteSource(id)
    }
}
