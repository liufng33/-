package com.app.persistence.data.remote.api

import com.app.persistence.data.remote.dto.SearchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SearchApiService {
    @GET
    suspend fun search(
        @Url url: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("filters") filters: Map<String, String>? = null
    ): Response<SearchResponseDto>
}
