package com.app.persistence.data.remote.api

import com.app.persistence.data.remote.dto.VideoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

data class ParseRequest(
    val url: String,
    val rules: Map<String, String>? = null
)

interface ParserApiService {
    @POST
    suspend fun parseVideoPage(
        @Url url: String,
        @Body request: ParseRequest
    ): Response<VideoDto>
}
