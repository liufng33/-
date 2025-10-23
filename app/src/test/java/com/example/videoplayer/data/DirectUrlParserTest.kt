package com.example.videoplayer.data

import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.model.StreamQuality
import com.example.videoplayer.data.parser.DirectUrlParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DirectUrlParserTest {

    private lateinit var parser: DirectUrlParser

    @Before
    fun setup() {
        parser = DirectUrlParser()
    }

    @Test
    fun `parser name is correct`() {
        assertEquals("Direct URL", parser.name)
    }

    @Test
    fun `parse mp4 URL returns success`() = runTest {
        val testUrl = "https://example.com/video.mp4"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
        val success = result as PlaybackOptions.Success
        assertEquals(1, success.qualities.size)
        assertEquals(testUrl, success.qualities[0].url)
        assertEquals("mp4", success.qualities[0].format)
    }

    @Test
    fun `parse m3u8 URL returns success`() = runTest {
        val testUrl = "https://example.com/video.m3u8"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
        val success = result as PlaybackOptions.Success
        assertEquals("m3u8", success.qualities[0].format)
    }

    @Test
    fun `parse webm URL returns success`() = runTest {
        val testUrl = "https://example.com/video.webm"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
    }

    @Test
    fun `parse URL with quality in name detects quality`() = runTest {
        val testUrl = "https://example.com/video_1080p.mp4"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
        val success = result as PlaybackOptions.Success
        assertEquals(StreamQuality.QUALITY_1080P, success.qualities[0].quality)
    }

    @Test
    fun `parse URL with 720p detects quality`() = runTest {
        val testUrl = "https://example.com/video_720p.mp4"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
        val success = result as PlaybackOptions.Success
        assertEquals(StreamQuality.QUALITY_720P, success.qualities[0].quality)
    }

    @Test
    fun `parse non-video URL returns error`() = runTest {
        val testUrl = "https://example.com/page.html"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Error)
        val error = result as PlaybackOptions.Error
        assertTrue(error.message.contains("does not appear to be a direct video link"))
    }

    @Test
    fun `parse extracts filename correctly`() = runTest {
        val testUrl = "https://example.com/videos/my_video.mp4?token=abc123"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
        val success = result as PlaybackOptions.Success
        assertEquals("my_video.mp4", success.title)
    }

    @Test
    fun `parse URL without quality defaults to auto`() = runTest {
        val testUrl = "https://example.com/video.mp4"
        val result = parser.parse(testUrl)
        
        assertTrue(result is PlaybackOptions.Success)
        val success = result as PlaybackOptions.Success
        assertEquals(StreamQuality.QUALITY_AUTO, success.qualities[0].quality)
    }
}
