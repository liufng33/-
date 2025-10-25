package com.remotedata.data.remote.parser

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JsoupHtmlParserTest {
    
    private lateinit var parser: HtmlParser
    
    @BeforeEach
    fun setup() {
        parser = JsoupHtmlParser()
    }
    
    @Test
    fun `parse extracts title from HTML`() {
        val html = """
            <html>
                <head>
                    <title>Test Page</title>
                </head>
                <body>
                    <p>Content</p>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals("Test Page", result.title)
        assertEquals("https://example.com", result.url)
        assertTrue(result.content.contains("Content"))
    }
    
    @Test
    fun `parse extracts metadata from HTML`() {
        val html = """
            <html>
                <head>
                    <title>Test</title>
                    <meta name="description" content="Test Description">
                    <meta property="og:title" content="OG Title">
                </head>
                <body></body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals("Test Description", result.metadata["description"])
        assertEquals("OG Title", result.metadata["og:title"])
    }
    
    @Test
    fun `parse extracts links from HTML`() {
        val html = """
            <html>
                <head><title>Test</title></head>
                <body>
                    <a href="/page1">Page 1</a>
                    <a href="https://example.com/page2">Page 2</a>
                    <a href="https://external.com">External</a>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertTrue(result.links.isNotEmpty())
        assertTrue(result.links.any { it.contains("page1") })
        assertTrue(result.links.any { it.contains("page2") })
        assertTrue(result.links.any { it.contains("external.com") })
    }
    
    @Test
    fun `parse handles empty HTML`() {
        val html = ""
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals("", result.title)
        assertEquals("", result.content)
        assertTrue(result.metadata.isEmpty())
        assertTrue(result.links.isEmpty())
    }
}
