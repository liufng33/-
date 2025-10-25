package com.app.persistence.data.repository

import com.app.persistence.data.cache.CacheManager
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.local.database.entity.SourceEntity
import com.app.persistence.data.remote.dto.PlaybackLinkDto
import com.app.persistence.data.remote.source.PlaybackRemoteDataSource
import com.app.persistence.domain.model.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaybackRepositoryImplTest {
    
    @MockK
    private lateinit var sourceDao: SourceDao
    
    @MockK
    private lateinit var playbackRemoteDataSource: PlaybackRemoteDataSource
    
    @MockK
    private lateinit var cacheManager: CacheManager
    
    private lateinit var repository: PlaybackRepositoryImpl
    
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = PlaybackRepositoryImpl(sourceDao, playbackRemoteDataSource, cacheManager)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `getPlaybackLinks returns cached non-expired links`() = runTest {
        // Given
        val video = createTestVideo()
        val cachedLinks = listOf(
            PlaybackLink(
                id = "1",
                url = "https://cdn.example.com/video1.mp4",
                quality = VideoQuality.HIGH,
                format = PlaybackFormat.MP4,
                videoId = "123",
                expiresAt = System.currentTimeMillis() + 60000 // Expires in 1 minute
            )
        )
        
        coEvery { cacheManager.get<List<PlaybackLink>>(any()) } returns cachedLinks
        
        // When
        val result = repository.getPlaybackLinks(video)
        
        // Then
        assertEquals(1, result.size)
        assertEquals(cachedLinks, result)
        coVerify(exactly = 0) { playbackRemoteDataSource.getPlaybackLinks(any(), any()) }
    }
    
    @Test
    fun `getPlaybackLinks fetches from remote when cache has expired links`() = runTest {
        // Given
        val video = createTestVideo()
        val expiredLinks = listOf(
            PlaybackLink(
                id = "1",
                url = "https://cdn.example.com/video1.mp4",
                quality = VideoQuality.HIGH,
                format = PlaybackFormat.MP4,
                videoId = "123",
                expiresAt = System.currentTimeMillis() - 1000 // Already expired
            )
        )
        val sourceEntity = SourceEntity(
            id = 123,
            name = "Test Source",
            type = SourceType.SEARCH.name,
            baseUrl = "https://api.example.com",
            parserClass = "com.example.Parser",
            isEnabled = true,
            priority = 1
        )
        val linkDto = PlaybackLinkDto(
            id = "2",
            url = "https://cdn.example.com/video2.mp4",
            quality = "HIGH",
            format = "MP4",
            videoId = "123"
        )
        
        coEvery { cacheManager.get<List<PlaybackLink>>(any()) } returns expiredLinks
        coEvery { sourceDao.getSourceById(123) } returns sourceEntity
        coEvery { playbackRemoteDataSource.getPlaybackLinks(any(), any()) } returns Result.Success(listOf(linkDto))
        coEvery { cacheManager.put(any(), any<List<PlaybackLink>>(), any()) } just Runs
        
        // When
        val result = repository.getPlaybackLinks(video)
        
        // Then
        assertEquals(1, result.size)
        assertEquals("2", result[0].id)
        coVerify { playbackRemoteDataSource.getPlaybackLinks(any(), any()) }
    }
    
    @Test
    fun `getPlaybackLinks returns empty list when remote fetch fails`() = runTest {
        // Given
        val video = createTestVideo()
        val sourceEntity = SourceEntity(
            id = 123,
            name = "Test Source",
            type = SourceType.SEARCH.name,
            baseUrl = "https://api.example.com",
            parserClass = "com.example.Parser",
            isEnabled = true,
            priority = 1
        )
        
        coEvery { cacheManager.get<List<PlaybackLink>>(any()) } returns null
        coEvery { sourceDao.getSourceById(123) } returns sourceEntity
        coEvery { playbackRemoteDataSource.getPlaybackLinks(any(), any()) } returns 
            Result.Error(DataError.NetworkError("Network error"))
        
        // When
        val result = repository.getPlaybackLinks(video)
        
        // Then
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `getPlaybackLink returns cached link when available and not expired`() = runTest {
        // Given
        val videoId = "123"
        val linkId = "1"
        val cachedLink = PlaybackLink(
            id = linkId,
            url = "https://cdn.example.com/video1.mp4",
            quality = VideoQuality.HIGH,
            format = PlaybackFormat.MP4,
            videoId = videoId,
            expiresAt = System.currentTimeMillis() + 60000
        )
        
        coEvery { cacheManager.get<PlaybackLink>(any()) } returns cachedLink
        
        // When
        val result = repository.getPlaybackLink(videoId, linkId)
        
        // Then
        assertNotNull(result)
        assertEquals(linkId, result.id)
    }
    
    @Test
    fun `getPlaybackLink fetches all links when cache miss`() = runTest {
        // Given
        val videoId = "123"
        val linkId = "1"
        val sourceEntity = SourceEntity(
            id = 123,
            name = "Test Source",
            type = SourceType.SEARCH.name,
            baseUrl = "https://api.example.com",
            parserClass = "com.example.Parser",
            isEnabled = true,
            priority = 1
        )
        val linkDto = PlaybackLinkDto(
            id = linkId,
            url = "https://cdn.example.com/video1.mp4",
            quality = "HIGH",
            format = "MP4",
            videoId = videoId
        )
        
        coEvery { cacheManager.get<PlaybackLink>(any()) } returns null
        coEvery { cacheManager.get<List<PlaybackLink>>(any()) } returns null
        coEvery { sourceDao.getSourceById(123) } returns sourceEntity
        coEvery { playbackRemoteDataSource.getPlaybackLinks(any(), any()) } returns Result.Success(listOf(linkDto))
        coEvery { cacheManager.put(any(), any<List<PlaybackLink>>(), any()) } just Runs
        
        // When
        val result = repository.getPlaybackLink(videoId, linkId)
        
        // Then
        assertNotNull(result)
        assertEquals(linkId, result.id)
    }
    
    @Test
    fun `refreshPlaybackLink invalidates cache and fetches new link`() = runTest {
        // Given
        val link = PlaybackLink(
            id = "1",
            url = "https://cdn.example.com/video1.mp4",
            quality = VideoQuality.HIGH,
            format = PlaybackFormat.MP4,
            videoId = "123",
            expiresAt = System.currentTimeMillis() - 1000
        )
        val sourceEntity = SourceEntity(
            id = 123,
            name = "Test Source",
            type = SourceType.SEARCH.name,
            baseUrl = "https://api.example.com",
            parserClass = "com.example.Parser",
            isEnabled = true,
            priority = 1
        )
        val refreshedLinkDto = PlaybackLinkDto(
            id = "1",
            url = "https://cdn.example.com/video1_refreshed.mp4",
            quality = "HIGH",
            format = "MP4",
            videoId = "123",
            expiresAt = System.currentTimeMillis() + 60000
        )
        
        coEvery { sourceDao.getSourceById(123) } returns sourceEntity
        coEvery { cacheManager.invalidate(any()) } just Runs
        coEvery { playbackRemoteDataSource.refreshPlaybackLink(any(), any()) } returns Result.Success(refreshedLinkDto)
        coEvery { cacheManager.put(any(), any<PlaybackLink>(), any()) } just Runs
        
        // When
        val result = repository.refreshPlaybackLink(link)
        
        // Then
        assertEquals("1", result.id)
        assertTrue(result.url.contains("refreshed"))
        coVerify(exactly = 2) { cacheManager.invalidate(any()) }
        coVerify { cacheManager.put(any(), any<PlaybackLink>(), any()) }
    }
    
    @Test
    fun `refreshPlaybackLink returns original link on refresh failure`() = runTest {
        // Given
        val link = PlaybackLink(
            id = "1",
            url = "https://cdn.example.com/video1.mp4",
            quality = VideoQuality.HIGH,
            format = PlaybackFormat.MP4,
            videoId = "123"
        )
        val sourceEntity = SourceEntity(
            id = 123,
            name = "Test Source",
            type = SourceType.SEARCH.name,
            baseUrl = "https://api.example.com",
            parserClass = "com.example.Parser",
            isEnabled = true,
            priority = 1
        )
        
        coEvery { sourceDao.getSourceById(123) } returns sourceEntity
        coEvery { cacheManager.invalidate(any()) } just Runs
        coEvery { playbackRemoteDataSource.refreshPlaybackLink(any(), any()) } returns 
            Result.Error(DataError.NetworkError("Refresh failed"))
        
        // When
        val result = repository.refreshPlaybackLink(link)
        
        // Then
        assertEquals(link, result)
    }
    
    private fun createTestVideo() = VideoItem(
        id = "123",
        title = "Test Video",
        url = "https://example.com/video/123",
        sourceId = "123"
    )
}
