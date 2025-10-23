package com.app.persistence.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.persistence.domain.model.CustomRule
import com.app.persistence.domain.model.RuleType

@Entity(
    tableName = "custom_rules",
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
        Index(value = ["rule_type"]),
        Index(value = ["is_enabled"]),
        Index(value = ["priority"])
    ]
)
data class CustomRuleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "source_id")
    val sourceId: Long,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "rule_type")
    val ruleType: String,
    
    @ColumnInfo(name = "pattern")
    val pattern: String,
    
    @ColumnInfo(name = "replacement")
    val replacement: String? = null,
    
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,
    
    @ColumnInfo(name = "priority")
    val priority: Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): CustomRule {
        return CustomRule(
            id = id,
            sourceId = sourceId,
            name = name,
            ruleType = RuleType.valueOf(ruleType),
            pattern = pattern,
            replacement = replacement,
            isEnabled = isEnabled,
            priority = priority,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromDomain(rule: CustomRule): CustomRuleEntity {
            return CustomRuleEntity(
                id = rule.id,
                sourceId = rule.sourceId,
                name = rule.name,
                ruleType = rule.ruleType.name,
                pattern = rule.pattern,
                replacement = rule.replacement,
                isEnabled = rule.isEnabled,
                priority = rule.priority,
                createdAt = rule.createdAt,
                updatedAt = rule.updatedAt
            )
        }
    }
}
