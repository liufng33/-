package com.app.persistence.domain.repository

import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.VideoItem

data class SearchOptions(
    val query: String,
    val limit: Int = 20,
    val offset: Int = 0,
    val filters: Map<String, String> = emptyMap()
)

data class SearchResult(
    val items: List<VideoItem>,
    val total: Int,
    val hasMore: Boolean
)

interface SearchRepository {
    suspend fun search(source: Source, options: SearchOptions): SearchResult
    
    suspend fun getActiveSearchSources(): List<Source>
    
    suspend fun healthCheck(source: Source): Boolean
}
