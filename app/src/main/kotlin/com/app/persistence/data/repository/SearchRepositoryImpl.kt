package com.app.persistence.data.repository

import com.app.persistence.data.cache.CacheManager
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.remote.source.SearchRemoteDataSource
import com.app.persistence.domain.model.DataError
import com.app.persistence.domain.model.Result
import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.SourceType
import com.app.persistence.domain.repository.SearchOptions
import com.app.persistence.domain.repository.SearchRepository
import com.app.persistence.domain.repository.SearchResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao,
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val cacheManager: CacheManager
) : SearchRepository {
    
    override suspend fun search(source: Source, options: SearchOptions): SearchResult {
        val cacheKey = buildCacheKey(source.id, options)
        
        // Try cache first
        cacheManager.get<SearchResult>(cacheKey)?.let { cached ->
            return cached
        }
        
        // Fetch from remote
        return when (val result = searchRemoteDataSource.search(
            source = source,
            query = options.query,
            limit = options.limit,
            offset = options.offset,
            filters = options.filters
        )) {
            is Result.Success -> {
                val searchResult = result.data.toDomain()
                // Cache the result
                cacheManager.put(cacheKey, searchResult, CacheManager.DEFAULT_TTL)
                searchResult
            }
            is Result.Error -> {
                // Return empty result on error
                SearchResult(items = emptyList(), total = 0, hasMore = false)
            }
        }
    }
    
    override suspend fun getActiveSearchSources(): List<Source> {
        return sourceDao.getSourcesByType(SourceType.SEARCH.name)
            .firstOrNull()
            ?.filter { it.isEnabled }
            ?.map { it.toDomain() }
            ?: emptyList()
    }
    
    override suspend fun healthCheck(source: Source): Boolean {
        return when (val result = searchRemoteDataSource.healthCheck(source)) {
            is Result.Success -> result.data
            is Result.Error -> false
        }
    }
    
    private fun buildCacheKey(sourceId: Long, options: SearchOptions): String {
        return "search:$sourceId:${options.query}:${options.limit}:${options.offset}"
    }
}
