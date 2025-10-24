package com.yingshi.video.domain.model

data class SourceConfig(
    val id: String,
    val name: String,
    val apiEndpoint: String,
    val type: SourceType,
    val parsingRules: List<ParseRule>,
    val isEnabled: Boolean = true,
    val priority: Int = 0,
    val headers: Map<String, String> = emptyMap(),
    val description: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class SourceType {
    SEARCH_SOURCE,
    PARSER_SOURCE
}
