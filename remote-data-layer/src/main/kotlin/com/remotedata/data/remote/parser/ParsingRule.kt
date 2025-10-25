package com.remotedata.data.remote.parser

data class ParsingRule(
    val selector: String,
    val attribute: String? = null,
    val extractor: ExtractionType = ExtractionType.TEXT
)

enum class ExtractionType {
    TEXT,
    HTML,
    ATTR
}

data class ParsingConfig(
    val titleRule: ParsingRule? = null,
    val contentRule: ParsingRule? = null,
    val linkRules: List<ParsingRule> = emptyList(),
    val metadataRules: Map<String, ParsingRule> = emptyMap()
)
