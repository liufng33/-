package com.sourcemanager.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SourceDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("isActive")
    val isActive: Boolean = false,
    @SerializedName("description")
    val description: String = ""
)

fun SourceDto.toDomain(): Source {
    return Source(
        id = id ?: UUID.randomUUID().toString(),
        name = name,
        type = try {
            SourceType.valueOf(type.uppercase())
        } catch (e: Exception) {
            SourceType.SEARCH
        },
        url = url,
        isActive = isActive,
        description = description
    )
}
