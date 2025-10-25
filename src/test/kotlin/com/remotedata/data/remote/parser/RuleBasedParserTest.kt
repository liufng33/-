package com.remotedata.data.remote.parser

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RuleBasedParserTest {
    
    @Test
    fun `parse with custom title rule`() {
        val config = ParsingConfig(
            titleRule = ParsingRule(
                selector = "h1",
                extractor = ExtractionType.TEXT
            )
        )
        val parser = RuleBasedParser(config)
        
        val html = """
            <html>
                <head><title>Default Title</title></head>
                <body>
                    <h1>Custom Title</h1>
                    <p>Content</p>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals("Custom Title", result.title)
    }
    
    @Test
    fun `parse with custom content rule`() {
        val config = ParsingConfig(
            contentRule = ParsingRule(
                selector = "article",
                extractor = ExtractionType.TEXT
            )
        )
        val parser = RuleBasedParser(config)
        
        val html = """
            <html>
                <body>
                    <nav>Navigation</nav>
                    <article>Article Content</article>
                    <footer>Footer</footer>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals("Article Content", result.content)
    }
    
    @Test
    fun `parse with custom link rules`() {
        val config = ParsingConfig(
            linkRules = listOf(
                ParsingRule(
                    selector = "article a",
                    attribute = "href",
                    extractor = ExtractionType.ATTR
                )
            )
        )
        val parser = RuleBasedParser(config)
        
        val html = """
            <html>
                <body>
                    <nav><a href="/nav-link">Nav</a></nav>
                    <article>
                        <a href="/article-link1">Link 1</a>
                        <a href="/article-link2">Link 2</a>
                    </article>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals(2, result.links.size)
        assertTrue(result.links.any { it.contains("article-link1") })
        assertTrue(result.links.any { it.contains("article-link2") })
        assertFalse(result.links.any { it.contains("nav-link") })
    }
    
    @Test
    fun `parse with metadata rules`() {
        val config = ParsingConfig(
            metadataRules = mapOf(
                "author" to ParsingRule(
                    selector = "meta[name=author]",
                    attribute = "content",
                    extractor = ExtractionType.ATTR
                ),
                "publishDate" to ParsingRule(
                    selector = ".publish-date",
                    extractor = ExtractionType.TEXT
                )
            )
        )
        val parser = RuleBasedParser(config)
        
        val html = """
            <html>
                <head>
                    <meta name="author" content="John Doe">
                </head>
                <body>
                    <div class="publish-date">2024-01-01</div>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertEquals("John Doe", result.metadata["author"])
        assertEquals("2024-01-01", result.metadata["publishDate"])
    }
    
    @Test
    fun `parse with HTML extraction type`() {
        val config = ParsingConfig(
            contentRule = ParsingRule(
                selector = "article",
                extractor = ExtractionType.HTML
            )
        )
        val parser = RuleBasedParser(config)
        
        val html = """
            <html>
                <body>
                    <article><p><strong>Bold</strong> text</p></article>
                </body>
            </html>
        """.trimIndent()
        
        val result = parser.parse(html, "https://example.com")
        
        assertTrue(result.content.contains("<p>"))
        assertTrue(result.content.contains("<strong>"))
    }
}
