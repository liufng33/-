package com.yingshi.video.domain.repository

import com.yingshi.video.domain.model.ParserConfig
import com.yingshi.video.domain.model.ParseRule
import com.yingshi.video.domain.model.SourceConfig
import kotlinx.coroutines.flow.Flow

interface SourceRepository {
    fun getAllSearchSources(): Flow<List<SourceConfig>>
    fun getEnabledSearchSources(): Flow<List<SourceConfig>>
    suspend fun getSearchSourceById(id: String): SourceConfig?
    suspend fun addSearchSource(source: SourceConfig)
    suspend fun updateSearchSource(source: SourceConfig)
    suspend fun deleteSearchSource(id: String)
    suspend fun setSearchSourceEnabled(id: String, enabled: Boolean)

    fun getAllParserSources(): Flow<List<ParserConfig>>
    fun getEnabledParserSources(): Flow<List<ParserConfig>>
    suspend fun getParserSourceById(id: String): ParserConfig?
    fun getParsersByDomain(domain: String): Flow<List<ParserConfig>>
    suspend fun addParserSource(parser: ParserConfig)
    suspend fun updateParserSource(parser: ParserConfig)
    suspend fun deleteParserSource(id: String)
    suspend fun setParserSourceEnabled(id: String, enabled: Boolean)

    fun getCustomRulesBySourceId(sourceId: String): Flow<List<ParseRule>>
    suspend fun addCustomRule(sourceId: String, rule: ParseRule)
    suspend fun updateCustomRule(rule: ParseRule)
    suspend fun deleteCustomRule(id: String)

    suspend fun refreshSourcesFromRemote(): Result<Unit>
}
