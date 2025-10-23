package com.cleanarch.domain.usecase

import com.cleanarch.domain.model.Result
import com.cleanarch.domain.repository.SampleRepository
import javax.inject.Inject

class GetSampleDataUseCase @Inject constructor(
    private val repository: SampleRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.fetchData()
    }
}
