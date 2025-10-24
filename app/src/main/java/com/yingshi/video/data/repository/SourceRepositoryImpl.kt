package com.yingshi.video.data.repository

import com.yingshi.video.data.local.dao.CustomRuleDao
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.entity.CustomRuleEntity
import com.yingshi.video.data.local.entity.ParserSourceEntity
import com.yingshi.video.data.local.entity.SearchSourceEntity
import com.yingshi.video.data.remote.ApiService
import com.yingshi.video.domain.model.ParserConfig
import com.yingshi.video.domain.model.ParseRule
import com.yingshi.video.domain.model.RuleType
import com.yingshi.video.domain.model.SourceConfig
import com.yingshi.video.domain.model.SourceType
import com.yingshi.video.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SourceRepositoryImpl @Inject constructor(
    private val searchSourceDao: SearchSourceDao,
    private val parserSourceDao: ParserSourceDao,
    private val customRuleDao: CustomRuleDao,
    private val apiService: ApiService
) : SourceRepository {

    override fun getAllSearchSources(): Flow<List<SourceConfig>> {
        return searchSourceDao.getAllSources().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getEnabledSearchSources(): Flow<List<SourceConfig>> {
        return searchSourceDao.getEnabledSources().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getSearchSourceById(id: String): SourceConfig? {
        return searchSourceDao.getSourceById(id)?.toDomainModel()
    }

    override suspend fun addSearchSource(source: SourceConfig) {
        searchSourceDao.insertSource(source.toSearchSourceEntity())
    }

    override suspend fun updateSearchSource(source: SourceConfig) {
        searchSourceDao.updateSource(source.toSearchSourceEntity())
    }

    override suspend fun deleteSearchSource(id: String) {
        searchSourceDao.deleteSourceById(id)
    }

    override suspend fun setSearchSourceEnabled(id: String, enabled: Boolean) {
        searchSourceDao.setSourceEnabled(id, enabled)
    }

    override fun getAllParserSources(): Flow<List<ParserConfig>> {
        return parserSourceDao.getAllParsers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getEnabledParserSources(): Flow<List<ParserConfig>> {
        return parserSourceDao.getEnabledParsers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getParserSourceById(id: String): ParserConfig? {
        return parserSourceDao.getParserById(id)?.toDomainModel()
    }

    override fun getParsersByDomain(domain: String): Flow<List<ParserConfig>> {
        return parserSourceDao.getParsersByDomain(domain).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addParserSource(parser: ParserConfig) {
        parserSourceDao.insertParser(parser.toEntity())
    }

    override suspend fun updateParserSource(parser: ParserConfig) {
        parserSourceDao.updateParser(parser.toEntity())
    }

    override suspend fun deleteParserSource(id: String) {
        parserSourceDao.deleteParserById(id)
    }

    override suspend fun setParserSourceEnabled(id: String, enabled: Boolean) {
        parserSourceDao.setParserEnabled(id, enabled)
    }

    override fun getCustomRulesBySourceId(sourceId: String): Flow<List<ParseRule>> {
        return customRuleDao.getRulesBySourceId(sourceId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addCustomRule(sourceId: String, rule: ParseRule) {
        customRuleDao.insertRule(rule.toEntity(sourceId))
    }

    override suspend fun updateCustomRule(rule: ParseRule) {
        val existingRule = customRuleDao.getRuleById(rule.id)
        if (existingRule != null) {
            customRuleDao.updateRule(rule.toEntity(existingRule.sourceId))
        }
    }

    override suspend fun deleteCustomRule(id: String) {
        customRuleDao.deleteRuleById(id)
    }

    override suspend fun refreshSourcesFromRemote(): Result<Unit> {
        return try {
            val response = apiService.getSourcesData()
            
            response.searchSources?.let { remoteSources ->
                val entities = remoteSources.map { remote ->
                    SearchSourceEntity(
                        id = remote.id,
                        name = remote.name,
                        apiEndpoint = remote.apiEndpoint,
                        isEnabled = remote.isEnabled ?: true,
                        priority = remote.priority ?: 0,
                        headers = remote.headers ?: emptyMap(),
                        description = remote.description,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
                searchSourceDao.insertSources(entities)
            }

            response.parserSources?.let { remoteParsers ->
                val entities = remoteParsers.map { remote ->
                    ParserSourceEntity(
                        id = remote.id,
                        name = remote.name,
                        parserUrl = remote.parserUrl,
                        supportedDomains = remote.supportedDomains,
                        isEnabled = remote.isEnabled ?: true,
                        priority = remote.priority ?: 0,
                        timeout = remote.timeout ?: 30000,
                        headers = remote.headers ?: emptyMap(),
                        description = remote.description,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
                parserSourceDao.insertParsers(entities)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun SearchSourceEntity.toDomainModel(): SourceConfig {
        val rules = customRuleDao.getRulesBySourceId(this.id)
        return SourceConfig(
            id = id,
            name = name,
            apiEndpoint = apiEndpoint,
            type = SourceType.SEARCH_SOURCE,
            parsingRules = emptyList(),
            isEnabled = isEnabled,
            priority = priority,
            headers = headers,
            description = description,
            lastUpdated = lastUpdated
        )
    }

    private fun SourceConfig.toSearchSourceEntity(): SearchSourceEntity {
        return SearchSourceEntity(
            id = id,
            name = name,
            apiEndpoint = apiEndpoint,
            isEnabled = isEnabled,
            priority = priority,
            headers = headers,
            description = description,
            lastUpdated = lastUpdated
        )
    }

    private fun ParserSourceEntity.toDomainModel(): ParserConfig {
        return ParserConfig(
            id = id,
            name = name,
            parserUrl = parserUrl,
            supportedDomains = supportedDomains,
            isEnabled = isEnabled,
            priority = priority,
            timeout = timeout,
            headers = headers,
            description = description,
            lastUpdated = lastUpdated
        )
    }

    private fun ParserConfig.toEntity(): ParserSourceEntity {
        return ParserSourceEntity(
            id = id,
            name = name,
            parserUrl = parserUrl,
            supportedDomains = supportedDomains,
            isEnabled = isEnabled,
            priority = priority,
            timeout = timeout,
            headers = headers,
            description = description,
            lastUpdated = lastUpdated
        )
    }

    private fun CustomRuleEntity.toDomainModel(): ParseRule {
        return ParseRule(
            id = id,
            name = name,
            ruleType = RuleType.valueOf(ruleType),
            selector = selector,
            attribute = attribute,
            regex = regex,
            replacement = replacement,
            isRequired = isRequired,
            defaultValue = defaultValue
        )
    }

    private fun ParseRule.toEntity(sourceId: String): CustomRuleEntity {
        return CustomRuleEntity(
            id = id,
            sourceId = sourceId,
            name = name,
            ruleType = ruleType.name,
            selector = selector,
            attribute = attribute,
            regex = regex,
            replacement = replacement,
            isRequired = isRequired,
            defaultValue = defaultValue
        )
    }
}
