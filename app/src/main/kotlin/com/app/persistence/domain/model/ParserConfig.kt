package com.app.persistence.domain.model

data class ParserConfig(
    val id: String,
    val name: String,
    val urlPattern: String,
    val baseUrl: String? = null,
    val rules: List<ParseRule> = emptyList(),
    val headers: Map<String, String> = emptyMap(),
    val timeout: Long = 30000,
    val enabled: Boolean = true
) {
    fun matchesUrl(url: String): Boolean {
        return try {
            Regex(urlPattern).containsMatchIn(url)
        } catch (e: Exception) {
            false
        }
    }

    fun getActiveRules(): List<ParseRule> {
        return rules.filter { it.enabled }.sortedBy { it.priority }
    }
}

data class ParseRule(
    val id: String,
    val name: String,
    val type: RuleType,
    val pattern: String,
    val extractField: String? = null,
    val priority: Int = 0,
    val enabled: Boolean = true
)

enum class RuleType {
    REGEX, CSS_SELECTOR, XPATH, JSON_PATH
}
