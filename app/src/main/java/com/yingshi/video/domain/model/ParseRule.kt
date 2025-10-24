package com.yingshi.video.domain.model

data class ParseRule(
    val id: String,
    val name: String,
    val ruleType: RuleType,
    val selector: String,
    val attribute: String? = null,
    val regex: String? = null,
    val replacement: String? = null,
    val isRequired: Boolean = true,
    val defaultValue: String? = null
)

enum class RuleType {
    CSS_SELECTOR,
    XPATH,
    JSON_PATH,
    REGEX
}
