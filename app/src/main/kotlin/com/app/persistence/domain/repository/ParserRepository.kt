package com.app.persistence.domain.repository

import com.app.persistence.domain.model.ParserConfig
import com.app.persistence.domain.model.VideoItem

interface ParserRepository {
    suspend fun findParserForUrl(url: String): ParserConfig?
    
    suspend fun getAllParsers(): List<ParserConfig>
    
    suspend fun getActiveParsers(): List<ParserConfig>
    
    suspend fun parseVideoPage(parser: ParserConfig, url: String): VideoItem?
}
