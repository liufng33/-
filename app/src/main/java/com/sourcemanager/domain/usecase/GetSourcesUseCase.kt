package com.sourcemanager.domain.usecase

import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSourcesUseCase @Inject constructor(
    private val repository: SourceRepository
) {
    operator fun invoke(): Flow<List<Source>> = repository.getSources()
}
