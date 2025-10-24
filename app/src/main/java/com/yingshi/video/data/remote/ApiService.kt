package com.yingshi.video.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api1.php")
    suspend fun getSourcesData(@Query("id") id: String = "3"): SourcesResponse
}
