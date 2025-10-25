package com.cleanarch.domain.repository

import com.cleanarch.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    suspend fun fetchData(): Result<String>
    fun observeData(): Flow<Result<String>>
}
