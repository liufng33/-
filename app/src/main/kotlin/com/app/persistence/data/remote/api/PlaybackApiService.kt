package com.app.persistence.data.remote.api

import com.app.persistence.data.remote.dto.PlaybackLinkDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface PlaybackApiService {
    @GET
    suspend fun getPlaybackLinks(
        @Url url: String,
        @Path("videoId") videoId: String
    ): Response<List<PlaybackLinkDto>>
    
    @POST
    suspend fun refreshPlaybackLink(
        @Url url: String
    ): Response<PlaybackLinkDto>
}
