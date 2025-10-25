package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.repository.SourceRepository
import javax.inject.Inject

class GetSourceByIdUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    suspend operator fun invoke(id: String): Source? {
        return repository.getSourceById(id)
    }
}
