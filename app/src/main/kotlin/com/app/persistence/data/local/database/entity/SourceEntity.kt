package com.app.persistence.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.SourceType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(
    tableName = "sources",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["type"]),
        Index(value = ["is_enabled"]),
        Index(value = ["priority"])
    ]
)
data class SourceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "type")
    val type: String,
    
    @ColumnInfo(name = "base_url")
    val baseUrl: String,
    
    @ColumnInfo(name = "parser_class")
    val parserClass: String,
    
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,
    
    @ColumnInfo(name = "priority")
    val priority: Int = 0,
    
    @ColumnInfo(name = "metadata")
    val metadata: String = "{}",
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Source {
        val gson = Gson()
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        val metadataMap: Map<String, String> = try {
            gson.fromJson(metadata, mapType)
        } catch (e: Exception) {
            emptyMap()
        }
        
        return Source(
            id = id,
            name = name,
            type = SourceType.valueOf(type),
            baseUrl = baseUrl,
            parserClass = parserClass,
            isEnabled = isEnabled,
            priority = priority,
            metadata = metadataMap,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromDomain(source: Source): SourceEntity {
            val gson = Gson()
            val metadataJson = gson.toJson(source.metadata)
            
            return SourceEntity(
                id = source.id,
                name = source.name,
                type = source.type.name,
                baseUrl = source.baseUrl,
                parserClass = source.parserClass,
                isEnabled = source.isEnabled,
                priority = source.priority,
                metadata = metadataJson,
                createdAt = source.createdAt,
                updatedAt = source.updatedAt
            )
        }
    }
}
