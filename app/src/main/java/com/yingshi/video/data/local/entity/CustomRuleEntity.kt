package com.yingshi.video.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "custom_rules",
    foreignKeys = [
        ForeignKey(
            entity = SearchSourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sourceId"])]
)
data class CustomRuleEntity(
    @PrimaryKey
    val id: String,
    val sourceId: String,
    val name: String,
    val ruleType: String,
    val selector: String,
    val attribute: String? = null,
    val regex: String? = null,
    val replacement: String? = null,
    val isRequired: Boolean = true,
    val defaultValue: String? = null
)
