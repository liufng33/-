package com.example.videoplayer.data.parser

import com.example.videoplayer.data.model.PlaybackOptions

interface VideoParser {
    suspend fun parse(url: String): PlaybackOptions
    val name: String
}

enum class ParserSource(val displayName: String) {
    JSOUP("Jsoup HTML Parser"),
    DIRECT("Direct URL"),
    REGEX("Regex Extractor")
}
