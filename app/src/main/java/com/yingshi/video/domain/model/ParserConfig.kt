package com.yingshi.video.domain.model

data class ParserConfig(
    val id: String,
    val name: String,
    val parserUrl: String,
    val supportedDomains: List<String>,
    val isEnabled: Boolean = true,
    val priority: Int = 0,
    val timeout: Long = 30000,
    val headers: Map<String, String> = emptyMap(),
    val description: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
