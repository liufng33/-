package com.yingshi.video.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.yingshi.video.data.local.database.Converters

@Entity(tableName = "search_sources")
@TypeConverters(Converters::class)
data class SearchSourceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val apiEndpoint: String,
    val isEnabled: Boolean = true,
    val priority: Int = 0,
    val headers: Map<String, String> = emptyMap(),
    val description: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
