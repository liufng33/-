package com.cleanarch.data.source.remote.api

import retrofit2.http.GET

interface ApiService {
    @GET("sample")
    suspend fun getSampleData(): String
}
