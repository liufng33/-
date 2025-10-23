package com.cleanarch.data.repository

import com.cleanarch.data.source.local.LocalDataSource
import com.cleanarch.data.source.remote.RemoteDataSource
import com.cleanarch.domain.model.Result
import com.cleanarch.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : SampleRepository {
    
    override suspend fun fetchData(): Result<String> {
        return try {
            val remoteData = remoteDataSource.fetchRemoteData()
            localDataSource.saveData(remoteData)
            Result.Success(remoteData)
        } catch (e: Exception) {
            Result.Error(e, "Failed to fetch data")
        }
    }
    
    override fun observeData(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            localDataSource.observeData().collect { data ->
                emit(Result.Success(data))
            }
        } catch (e: Exception) {
            emit(Result.Error(e, "Failed to observe data"))
        }
    }
}
