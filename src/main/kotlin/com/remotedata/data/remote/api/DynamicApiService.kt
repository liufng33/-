package com.remotedata.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface DynamicApiService {
    
    @GET
    suspend fun fetchDynamicContent(
        @Url url: String
    ): Response<String>
}
