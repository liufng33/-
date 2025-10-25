package com.remotedata.data.remote.api

import com.remotedata.data.remote.dto.ApiResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    
    @GET("api1.php")
    suspend fun getApiData(
        @Query("id") id: String
    ): Response<ApiResponseDto>
    
    @GET("api1.php")
    suspend fun getApiDataAsString(
        @Query("id") id: String
    ): Response<String>
}
