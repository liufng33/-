package com.sourcemanager.data.remote.api

import com.sourcemanager.data.remote.dto.SourceDto
import retrofit2.http.GET
import retrofit2.http.Url

interface SourceApiService {
    
    @GET
    suspend fun importSources(@Url url: String): List<SourceDto>
}
