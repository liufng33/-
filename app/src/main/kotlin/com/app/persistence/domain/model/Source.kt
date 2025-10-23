package com.app.persistence.domain.model

data class Source(
    val id: Long = 0,
    val name: String,
    val type: SourceType,
    val baseUrl: String,
    val parserClass: String,
    val isEnabled: Boolean = true,
    val priority: Int = 0,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class SourceType {
    SEARCH,
    PARSER,
    HYBRID
}
