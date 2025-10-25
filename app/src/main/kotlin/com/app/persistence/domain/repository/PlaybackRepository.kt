package com.app.persistence.domain.repository

import com.app.persistence.domain.model.PlaybackLink
import com.app.persistence.domain.model.VideoItem

interface PlaybackRepository {
    suspend fun getPlaybackLinks(video: VideoItem): List<PlaybackLink>
    
    suspend fun getPlaybackLink(videoId: String, linkId: String): PlaybackLink?
    
    suspend fun refreshPlaybackLink(link: PlaybackLink): PlaybackLink
}
