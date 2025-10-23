package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow

class GetSourcesUseCase(private val repository: SourceRepository) {
    operator fun invoke(): Flow<List<Source>> = repository.getSources()
}
