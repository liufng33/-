package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.repository.SourceRepository

class SwitchActiveSourceUseCase(private val repository: SourceRepository) {
    suspend operator fun invoke(id: String, type: SourceType): Result<Unit> {
        return repository.switchActiveSource(id, type)
    }
}
