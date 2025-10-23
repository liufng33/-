package com.app.persistence.domain.model

data class UserSelection(
    val id: Long = 0,
    val sourceId: Long,
    val itemId: String,
    val itemType: SelectionType,
    val title: String,
    val metadata: Map<String, String> = emptyMap(),
    val selectedAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis()
)

enum class SelectionType {
    FAVORITE,
    BOOKMARK,
    HISTORY,
    DOWNLOAD
}
