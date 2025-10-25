package com.remotedata.data.mapper

import com.remotedata.data.remote.dto.SearchItemDto
import com.remotedata.data.remote.dto.SearchResponseDto
import com.remotedata.domain.entity.SearchResult

object SearchResponseMapper {
    
    fun SearchItemDto.toDomain(): SearchResult {
        return SearchResult(
            id = this.id ?: "",
            title = this.title ?: "",
            description = this.description,
            url = this.url ?: "",
            relevanceScore = this.score ?: 0.0
        )
    }
    
    fun SearchResponseDto.toDomain(): List<SearchResult> {
        return this.results?.map { it.toDomain() } ?: emptyList()
    }
}
