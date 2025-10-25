package com.remotedata.domain.entity

data class SearchResult(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val relevanceScore: Double = 0.0
)
