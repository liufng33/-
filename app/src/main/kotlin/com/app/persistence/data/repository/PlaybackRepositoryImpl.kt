package com.app.persistence.data.repository

import com.app.persistence.data.cache.CacheManager
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.remote.source.PlaybackRemoteDataSource
import com.app.persistence.domain.model.PlaybackLink
import com.app.persistence.domain.model.Result
import com.app.persistence.domain.model.VideoItem
import com.app.persistence.domain.repository.PlaybackRepository
import javax.inject.Inject

class PlaybackRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao,
    private val playbackRemoteDataSource: PlaybackRemoteDataSource,
    private val cacheManager: CacheManager
) : PlaybackRepository {
    
    override suspend fun getPlaybackLinks(video: VideoItem): List<PlaybackLink> {
        val cacheKey = "playback:${video.id}"
        
        // Try cache first, but only if not expired
        cacheManager.get<List<PlaybackLink>>(cacheKey)?.let { cached ->
            // Filter out expired links
            val validLinks = cached.filter { !it.isExpired() }
            if (validLinks.isNotEmpty()) {
                return validLinks
            }
        }
        
        // Get source for base URL
        val source = sourceDao.getSourceById(video.sourceId.toLongOrNull() ?: 0)
        val baseUrl = source?.baseUrl ?: return emptyList()
        
        // Fetch from remote
        return when (val result = playbackRemoteDataSource.getPlaybackLinks(video, baseUrl)) {
            is Result.Success -> {
                val playbackLinks = result.data.map { it.toDomain() }
                // Cache with shorter TTL since playback links can expire
                cacheManager.put(cacheKey, playbackLinks, CacheManager.SHORT_TTL)
                playbackLinks
            }
            is Result.Error -> emptyList()
        }
    }
    
    override suspend fun getPlaybackLink(videoId: String, linkId: String): PlaybackLink? {
        val cacheKey = "playback:$videoId:$linkId"
        
        // Try cache first
        cacheManager.get<PlaybackLink>(cacheKey)?.let { cached ->
            if (!cached.isExpired()) {
                return cached
            }
        }
        
        // If not in cache, get all links and find the requested one
        val video = VideoItem(
            id = videoId,
            title = "",
            url = "",
            sourceId = ""
        )
        return getPlaybackLinks(video).find { it.id == linkId }
    }
    
    override suspend fun refreshPlaybackLink(link: PlaybackLink): PlaybackLink {
        // Get source for base URL
        val source = sourceDao.getSourceById(link.videoId.toLongOrNull() ?: 0)
        val baseUrl = source?.baseUrl ?: throw IllegalStateException("Source not found")
        
        // Invalidate cache for this link
        cacheManager.invalidate("playback:${link.videoId}:${link.id}")
        cacheManager.invalidate("playback:${link.videoId}")
        
        // Refresh from remote
        return when (val result = playbackRemoteDataSource.refreshPlaybackLink(link, baseUrl)) {
            is Result.Success -> {
                val refreshedLink = result.data.toDomain()
                // Cache the refreshed link
                cacheManager.put(
                    "playback:${refreshedLink.videoId}:${refreshedLink.id}",
                    refreshedLink,
                    CacheManager.SHORT_TTL
                )
                refreshedLink
            }
            is Result.Error -> {
                // Return original link if refresh fails
                link
            }
        }
    }
}
