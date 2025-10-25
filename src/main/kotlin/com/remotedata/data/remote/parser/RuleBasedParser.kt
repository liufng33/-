package com.remotedata.data.remote.parser

import com.remotedata.domain.entity.ParsedContent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class RuleBasedParser(private val config: ParsingConfig) : HtmlParser {
    
    override fun parse(html: String, baseUrl: String): ParsedContent {
        val document: Document = Jsoup.parse(html, baseUrl)
        
        return ParsedContent(
            url = baseUrl,
            title = extractTitle(document),
            content = extractContent(document),
            metadata = extractMetadata(document),
            links = extractLinks(document)
        )
    }
    
    private fun extractTitle(document: Document): String? {
        return config.titleRule?.let { rule ->
            applyRule(document, rule)
        } ?: document.title()
    }
    
    private fun extractContent(document: Document): String {
        return config.contentRule?.let { rule ->
            applyRule(document, rule)
        } ?: document.body()?.text() ?: ""
    }
    
    private fun extractMetadata(document: Document): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        
        config.metadataRules.forEach { (key, rule) ->
            val value = applyRule(document, rule)
            if (value.isNotEmpty()) {
                metadata[key] = value
            }
        }
        
        return metadata
    }
    
    private fun extractLinks(document: Document): List<String> {
        if (config.linkRules.isEmpty()) {
            return document.select("a[href]")
                .mapNotNull { it.attr("abs:href").takeIf { url -> url.isNotEmpty() } }
                .distinct()
        }
        
        return config.linkRules.flatMap { rule ->
            document.select(rule.selector).mapNotNull { element ->
                when (rule.extractor) {
                    ExtractionType.ATTR -> {
                        val attr = rule.attribute ?: "href"
                        element.attr("abs:$attr").takeIf { it.isNotEmpty() }
                    }
                    else -> element.attr("abs:href").takeIf { it.isNotEmpty() }
                }
            }
        }.distinct()
    }
    
    private fun applyRule(document: Document, rule: ParsingRule): String {
        val elements = document.select(rule.selector)
        
        return when (rule.extractor) {
            ExtractionType.TEXT -> elements.text()
            ExtractionType.HTML -> elements.html()
            ExtractionType.ATTR -> {
                val attr = rule.attribute ?: return ""
                elements.attr(attr)
            }
        }
    }
}
