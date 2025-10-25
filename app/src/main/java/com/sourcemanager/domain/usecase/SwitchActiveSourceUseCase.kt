package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.repository.SourceRepository
import javax.inject.Inject

class SwitchActiveSourceUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    suspend operator fun invoke(id: String, type: SourceType): Result<Unit> {
        return repository.switchActiveSource(id, type)
    }
}
