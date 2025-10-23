package com.sourcemanager.domain.model

data class Source(
    val id: String,
    val name: String,
    val type: SourceType,
    val url: String,
    val isActive: Boolean = false,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class SourceType {
    SEARCH,
    PARSER
}
