package com.remotedata.domain.entity

data class ParsedContent(
    val url: String,
    val title: String?,
    val content: String,
    val metadata: Map<String, String> = emptyMap(),
    val links: List<String> = emptyList()
)
