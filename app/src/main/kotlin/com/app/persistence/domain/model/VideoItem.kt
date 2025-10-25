package com.app.persistence.domain.model

data class VideoItem(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String? = null,
    val duration: Int? = null,
    val description: String? = null,
    val sourceId: String,
    val publishDate: Long? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    init {
        require(id.isNotBlank()) { "VideoItem ID cannot be empty" }
        require(title.isNotBlank()) { "VideoItem title cannot be empty" }
        require(sourceId.isNotBlank()) { "VideoItem sourceId cannot be empty" }
        require(duration == null || duration >= 0) { "VideoItem duration cannot be negative" }
    }
}
