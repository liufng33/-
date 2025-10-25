package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.repository.SourceRepository
import javax.inject.Inject

class DeleteSourceUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteSource(id)
    }
}
