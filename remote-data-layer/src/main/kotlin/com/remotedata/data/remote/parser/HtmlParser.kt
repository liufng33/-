package com.remotedata.data.remote.parser

import com.remotedata.domain.entity.ParsedContent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface HtmlParser {
    fun parse(html: String, baseUrl: String): ParsedContent
}

class JsoupHtmlParser : HtmlParser {
    
    override fun parse(html: String, baseUrl: String): ParsedContent {
        val document: Document = Jsoup.parse(html, baseUrl)
        
        return ParsedContent(
            url = baseUrl,
            title = document.title(),
            content = document.body()?.text() ?: "",
            metadata = extractMetadata(document),
            links = extractLinks(document, baseUrl)
        )
    }
    
    private fun extractMetadata(document: Document): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        
        document.select("meta").forEach { meta ->
            val name = meta.attr("name").ifEmpty { meta.attr("property") }
            val content = meta.attr("content")
            if (name.isNotEmpty() && content.isNotEmpty()) {
                metadata[name] = content
            }
        }
        
        return metadata
    }
    
    private fun extractLinks(document: Document, baseUrl: String): List<String> {
        return document.select("a[href]")
            .mapNotNull { element ->
                val href = element.attr("abs:href")
                if (href.isNotEmpty()) href else null
            }
            .distinct()
    }
}
