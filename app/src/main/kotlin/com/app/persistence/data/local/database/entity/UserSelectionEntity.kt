package com.app.persistence.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.persistence.domain.model.SelectionType
import com.app.persistence.domain.model.UserSelection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(
    tableName = "user_selections",
    foreignKeys = [
        ForeignKey(
            entity = SourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["source_id"]),
        Index(value = ["item_id"]),
        Index(value = ["item_type"]),
        Index(value = ["selected_at"]),
        Index(value = ["last_accessed_at"])
    ]
)
data class UserSelectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "source_id")
    val sourceId: Long,
    
    @ColumnInfo(name = "item_id")
    val itemId: String,
    
    @ColumnInfo(name = "item_type")
    val itemType: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "metadata")
    val metadata: String = "{}",
    
    @ColumnInfo(name = "selected_at")
    val selectedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "last_accessed_at")
    val lastAccessedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): UserSelection {
        val gson = Gson()
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        val metadataMap: Map<String, String> = try {
            gson.fromJson(metadata, mapType)
        } catch (e: Exception) {
            emptyMap()
        }
        
        return UserSelection(
            id = id,
            sourceId = sourceId,
            itemId = itemId,
            itemType = SelectionType.valueOf(itemType),
            title = title,
            metadata = metadataMap,
            selectedAt = selectedAt,
            lastAccessedAt = lastAccessedAt
        )
    }
    
    companion object {
        fun fromDomain(selection: UserSelection): UserSelectionEntity {
            val gson = Gson()
            val metadataJson = gson.toJson(selection.metadata)
            
            return UserSelectionEntity(
                id = selection.id,
                sourceId = selection.sourceId,
                itemId = selection.itemId,
                itemType = selection.itemType.name,
                title = selection.title,
                metadata = metadataJson,
                selectedAt = selection.selectedAt,
                lastAccessedAt = selection.lastAccessedAt
            )
        }
    }
}
