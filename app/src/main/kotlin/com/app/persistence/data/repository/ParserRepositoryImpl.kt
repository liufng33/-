package com.app.persistence.data.repository

import com.app.persistence.data.cache.CacheManager
import com.app.persistence.data.local.database.dao.CustomRuleDao
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.remote.source.ParserRemoteDataSource
import com.app.persistence.domain.model.*
import com.app.persistence.domain.repository.ParserRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ParserRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao,
    private val customRuleDao: CustomRuleDao,
    private val parserRemoteDataSource: ParserRemoteDataSource,
    private val cacheManager: CacheManager
) : ParserRepository {
    
    override suspend fun findParserForUrl(url: String): ParserConfig? {
        val cacheKey = "parser:match:$url"
        
        // Try cache first
        cacheManager.get<ParserConfig>(cacheKey)?.let { return it }
        
        // Get all parser sources from local DB
        val parserSources = sourceDao.getSourcesByType(SourceType.PARSER.name)
            .firstOrNull()
            ?.filter { it.isEnabled }
            ?.map { it.toDomain() }
            ?: return null
        
        // Find matching parser by URL pattern
        for (source in parserSources) {
            val rules = customRuleDao.getRulesBySourceId(source.id)
                .map { ruleEntity ->
                    ParseRule(
                        id = ruleEntity.id.toString(),
                        name = ruleEntity.name,
                        type = parseRuleType(ruleEntity.ruleType),
                        pattern = ruleEntity.pattern,
                        extractField = ruleEntity.replacement,
                        priority = ruleEntity.priority,
                        enabled = ruleEntity.isEnabled
                    )
                }
            
            val parser = ParserConfig(
                id = source.id.toString(),
                name = source.name,
                urlPattern = source.parserClass, // Using parserClass as URL pattern
                baseUrl = source.baseUrl,
                rules = rules,
                enabled = source.isEnabled
            )
            
            if (parser.matchesUrl(url)) {
                // Cache the matched parser
                cacheManager.put(cacheKey, parser, CacheManager.LONG_TTL)
                return parser
            }
        }
        
        return null
    }
    
    override suspend fun getAllParsers(): List<ParserConfig> {
        val cacheKey = "parsers:all"
        
        // Try cache first
        cacheManager.get<List<ParserConfig>>(cacheKey)?.let { return it }
        
        val parserSources = sourceDao.getSourcesByType(SourceType.PARSER.name)
            .firstOrNull()
            ?.map { it.toDomain() }
            ?: return emptyList()
        
        val parsers = parserSources.map { source ->
            val rules = customRuleDao.getRulesBySourceId(source.id)
                .map { ruleEntity ->
                    ParseRule(
                        id = ruleEntity.id.toString(),
                        name = ruleEntity.name,
                        type = parseRuleType(ruleEntity.ruleType),
                        pattern = ruleEntity.pattern,
                        extractField = ruleEntity.replacement,
                        priority = ruleEntity.priority,
                        enabled = ruleEntity.isEnabled
                    )
                }
            
            ParserConfig(
                id = source.id.toString(),
                name = source.name,
                urlPattern = source.parserClass,
                baseUrl = source.baseUrl,
                rules = rules,
                enabled = source.isEnabled
            )
        }
        
        // Cache the result
        cacheManager.put(cacheKey, parsers, CacheManager.LONG_TTL)
        return parsers
    }
    
    override suspend fun getActiveParsers(): List<ParserConfig> {
        return getAllParsers().filter { it.enabled }
    }
    
    override suspend fun parseVideoPage(parser: ParserConfig, url: String): VideoItem? {
        val cacheKey = "parse:${parser.id}:$url"
        
        // Try cache first
        cacheManager.get<VideoItem>(cacheKey)?.let { return it }
        
        // Parse using remote data source
        return when (val result = parserRemoteDataSource.parseVideoPage(parser, url)) {
            is Result.Success -> {
                val videoItem = result.data.toDomain()
                // Cache the parsed result
                cacheManager.put(cacheKey, videoItem, CacheManager.DEFAULT_TTL)
                videoItem
            }
            is Result.Error -> null
        }
    }
    
    private fun parseRuleType(ruleType: String): RuleType {
        return when (ruleType.uppercase()) {
            "REGEX" -> RuleType.REGEX
            "CSS_SELECTOR", "CSS" -> RuleType.CSS_SELECTOR
            "XPATH" -> RuleType.XPATH
            "JSON_PATH", "JSON" -> RuleType.JSON_PATH
            else -> RuleType.REGEX
        }
    }
}
