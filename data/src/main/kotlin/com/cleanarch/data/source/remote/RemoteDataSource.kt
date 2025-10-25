package com.cleanarch.data.source.remote

import com.cleanarch.data.source.remote.api.ApiService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchRemoteData(): String {
        return "Sample Remote Data"
    }
}
