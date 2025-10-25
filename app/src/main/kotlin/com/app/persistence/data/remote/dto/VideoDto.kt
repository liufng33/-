package com.app.persistence.data.remote.dto

import com.app.persistence.domain.model.VideoItem
import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("url")
    val url: String,
    
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,
    
    @SerializedName("duration")
    val duration: Int? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("source_id")
    val sourceId: String,
    
    @SerializedName("publish_date")
    val publishDate: Long? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null
) {
    fun toDomain(): VideoItem {
        return VideoItem(
            id = id,
            title = title,
            url = url,
            thumbnailUrl = thumbnailUrl,
            duration = duration,
            description = description,
            sourceId = sourceId,
            publishDate = publishDate,
            metadata = metadata ?: emptyMap()
        )
    }
}
