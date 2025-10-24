package com.yingshi.video.domain.model

data class VideoItem(
    val id: String,
    val title: String,
    val coverUrl: String?,
    val videoUrl: String?,
    val description: String?,
    val duration: String?,
    val category: String?,
    val rating: Float?,
    val releaseDate: String?,
    val sourceId: String,
    val metadata: Map<String, String> = emptyMap()
)
