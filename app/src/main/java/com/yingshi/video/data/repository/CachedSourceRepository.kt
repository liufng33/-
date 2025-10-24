package com.yingshi.video.data.repository

import com.yingshi.video.domain.model.ParserConfig
import com.yingshi.video.domain.model.ParseRule
import com.yingshi.video.domain.model.SourceConfig
import com.yingshi.video.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachedSourceRepository @Inject constructor(
    private val delegate: SourceRepositoryImpl
) : SourceRepository {

    private val searchSourcesCache = MutableStateFlow<List<SourceConfig>?>(null)
    private val parserSourcesCache = MutableStateFlow<List<ParserConfig>?>(null)
    private val cacheMutex = Mutex()

    override fun getAllSearchSources(): Flow<List<SourceConfig>> {
        return delegate.getAllSearchSources()
    }

    override fun getEnabledSearchSources(): Flow<List<SourceConfig>> {
        return delegate.getEnabledSearchSources()
    }

    override suspend fun getSearchSourceById(id: String): SourceConfig? {
        return searchSourcesCache.value?.find { it.id == id }
            ?: delegate.getSearchSourceById(id)
    }

    override suspend fun addSearchSource(source: SourceConfig) {
        delegate.addSearchSource(source)
        invalidateSearchCache()
    }

    override suspend fun updateSearchSource(source: SourceConfig) {
        delegate.updateSearchSource(source)
        invalidateSearchCache()
    }

    override suspend fun deleteSearchSource(id: String) {
        delegate.deleteSearchSource(id)
        invalidateSearchCache()
    }

    override suspend fun setSearchSourceEnabled(id: String, enabled: Boolean) {
        delegate.setSearchSourceEnabled(id, enabled)
        invalidateSearchCache()
    }

    override fun getAllParserSources(): Flow<List<ParserConfig>> {
        return delegate.getAllParserSources()
    }

    override fun getEnabledParserSources(): Flow<List<ParserConfig>> {
        return delegate.getEnabledParserSources()
    }

    override suspend fun getParserSourceById(id: String): ParserConfig? {
        return parserSourcesCache.value?.find { it.id == id }
            ?: delegate.getParserSourceById(id)
    }

    override fun getParsersByDomain(domain: String): Flow<List<ParserConfig>> {
        return delegate.getParsersByDomain(domain)
    }

    override suspend fun addParserSource(parser: ParserConfig) {
        delegate.addParserSource(parser)
        invalidateParserCache()
    }

    override suspend fun updateParserSource(parser: ParserConfig) {
        delegate.updateParserSource(parser)
        invalidateParserCache()
    }

    override suspend fun deleteParserSource(id: String) {
        delegate.deleteParserSource(id)
        invalidateParserCache()
    }

    override suspend fun setParserSourceEnabled(id: String, enabled: Boolean) {
        delegate.setParserSourceEnabled(id, enabled)
        invalidateParserCache()
    }

    override fun getCustomRulesBySourceId(sourceId: String): Flow<List<ParseRule>> {
        return delegate.getCustomRulesBySourceId(sourceId)
    }

    override suspend fun addCustomRule(sourceId: String, rule: ParseRule) {
        delegate.addCustomRule(sourceId, rule)
    }

    override suspend fun updateCustomRule(rule: ParseRule) {
        delegate.updateCustomRule(rule)
    }

    override suspend fun deleteCustomRule(id: String) {
        delegate.deleteCustomRule(id)
    }

    override suspend fun refreshSourcesFromRemote(): Result<Unit> {
        val result = delegate.refreshSourcesFromRemote()
        if (result.isSuccess) {
            invalidateSearchCache()
            invalidateParserCache()
        }
        return result
    }

    private suspend fun invalidateSearchCache() {
        cacheMutex.withLock {
            searchSourcesCache.value = null
        }
    }

    private suspend fun invalidateParserCache() {
        cacheMutex.withLock {
            parserSourcesCache.value = null
        }
    }
}
