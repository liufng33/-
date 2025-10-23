package com.example.videoplayer.data

import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.parser.JsoupParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JsoupParserTest {

    private lateinit var parser: JsoupParser

    @Before
    fun setup() {
        parser = JsoupParser()
    }

    @Test
    fun `parser name is correct`() {
        assertEquals("Jsoup HTML Parser", parser.name)
    }

    @Test
    fun `parse with invalid URL returns error`() = runTest {
        val result = parser.parse("not-a-valid-url")
        
        assertTrue(result is PlaybackOptions.Error)
    }

    @Test
    fun `parse handles connection timeout gracefully`() = runTest {
        val result = parser.parse("https://this-domain-does-not-exist-12345.com")
        
        assertTrue(result is PlaybackOptions.Error)
        assertTrue((result as PlaybackOptions.Error).message.contains("Failed to parse URL"))
    }
}
