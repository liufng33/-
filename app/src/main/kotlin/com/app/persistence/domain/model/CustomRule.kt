package com.app.persistence.domain.model

data class CustomRule(
    val id: Long = 0,
    val sourceId: Long,
    val name: String,
    val ruleType: RuleType,
    val pattern: String,
    val replacement: String? = null,
    val isEnabled: Boolean = true,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class RuleType {
    FILTER,
    TRANSFORM,
    VALIDATION,
    EXTRACTION
}
