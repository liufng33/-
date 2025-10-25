package com.app.persistence.data.remote.dto

import com.app.persistence.domain.repository.SearchResult
import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("items")
    val items: List<VideoDto>,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("has_more")
    val hasMore: Boolean
) {
    fun toDomain(): SearchResult {
        return SearchResult(
            items = items.map { it.toDomain() },
            total = total,
            hasMore = hasMore
        )
    }
}
