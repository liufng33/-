package com.yingshi.video.data.local.seeding

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.entity.ParserSourceEntity
import com.yingshi.video.data.local.entity.SearchSourceEntity
import com.yingshi.video.data.remote.ApiService
import com.yingshi.video.data.remote.SourcesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DataSeeder(
    private val context: Context,
    private val apiService: ApiService,
    private val searchSourceDao: SearchSourceDao,
    private val parserSourceDao: ParserSourceDao
) {
    private val gson = Gson()

    suspend fun seedInitialData(forceRefresh: Boolean = false) {
        withContext(Dispatchers.IO) {
            val searchSourceCount = searchSourceDao.getSourceCount()
            val parserSourceCount = parserSourceDao.getParserCount()

            if (!forceRefresh && searchSourceCount > 0 && parserSourceCount > 0) {
                Log.d(TAG, "Data already seeded, skipping")
                return@withContext
            }

            Log.d(TAG, "Starting data seeding...")

            val sourcesData = try {
                fetchFromApi()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch from API, trying bundled data: ${e.message}")
                loadFromBundledJson()
            }

            if (sourcesData != null) {
                seedSearchSources(sourcesData)
                seedParserSources(sourcesData)
                Log.d(TAG, "Data seeding completed successfully")
            } else {
                Log.e(TAG, "Failed to seed data from any source")
            }
        }
    }

    private suspend fun fetchFromApi(): SourcesResponse? {
        return try {
            Log.d(TAG, "Fetching sources from API...")
            apiService.getSourcesData()
        } catch (e: Exception) {
            Log.e(TAG, "API fetch failed: ${e.message}", e)
            null
        }
    }

    private fun loadFromBundledJson(): SourcesResponse? {
        return try {
            Log.d(TAG, "Loading sources from bundled JSON...")
            val jsonString = context.assets.open("default_sources.json")
                .bufferedReader()
                .use { it.readText() }
            gson.fromJson(jsonString, SourcesResponse::class.java)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to load bundled JSON: ${e.message}", e)
            null
        }
    }

    private suspend fun seedSearchSources(sourcesData: SourcesResponse) {
        val searchSources = sourcesData.searchSources?.map { remote ->
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
        } ?: emptyList()

        if (searchSources.isNotEmpty()) {
            searchSourceDao.insertSources(searchSources)
            Log.d(TAG, "Seeded ${searchSources.size} search sources")
        }
    }

    private suspend fun seedParserSources(sourcesData: SourcesResponse) {
        val parserSources = sourcesData.parserSources?.map { remote ->
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
        } ?: emptyList()

        if (parserSources.isNotEmpty()) {
            parserSourceDao.insertParsers(parserSources)
            Log.d(TAG, "Seeded ${parserSources.size} parser sources")
        }
    }

    companion object {
        private const val TAG = "DataSeeder"
    }
}
