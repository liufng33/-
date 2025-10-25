package com.sourcemanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val url: String,
    val isActive: Boolean = false,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun SourceEntity.toDomain(): Source {
    return Source(
        id = id,
        name = name,
        type = SourceType.valueOf(type),
        url = url,
        isActive = isActive,
        description = description,
        createdAt = createdAt
    )
}

fun Source.toEntity(): SourceEntity {
    return SourceEntity(
        id = id,
        name = name,
        type = type.name,
        url = url,
        isActive = isActive,
        description = description,
        createdAt = createdAt
    )
}
