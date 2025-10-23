package com.app.persistence.data.local.database

import android.content.Context
import com.app.persistence.domain.model.Source
import com.app.persistence.domain.model.SourceType
import com.app.persistence.domain.repository.SourceRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject

class DataSeeder @Inject constructor(
    private val context: Context,
    private val sourceRepository: SourceRepository
) {
    
    data class SourceJson(
        val name: String,
        val type: String,
        val baseUrl: String,
        val parserClass: String,
        val isEnabled: Boolean = true,
        val priority: Int = 0,
        val metadata: Map<String, String> = emptyMap()
    )
    
    suspend fun seedInitialData() = withContext(Dispatchers.IO) {
        val sourceCount = sourceRepository.getSourceCount()
        if (sourceCount == 0) {
            seedSourcesFromAssets()
        }
    }
    
    private suspend fun seedSourcesFromAssets() {
        try {
            val inputStream = context.assets.open("sources.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val listType = object : TypeToken<List<SourceJson>>() {}.type
            val sourcesJson: List<SourceJson> = gson.fromJson(reader, listType)
            
            val sources = sourcesJson.map { json ->
                Source(
                    name = json.name,
                    type = SourceType.valueOf(json.type),
                    baseUrl = json.baseUrl,
                    parserClass = json.parserClass,
                    isEnabled = json.isEnabled,
                    priority = json.priority,
                    metadata = json.metadata
                )
            }
            
            sourceRepository.insertSources(sources)
            reader.close()
        } catch (e: Exception) {
            seedDefaultSources()
        }
    }
    
    private suspend fun seedDefaultSources() {
        val defaultSources = listOf(
            Source(
                name = "Default Search Source",
                type = SourceType.SEARCH,
                baseUrl = "https://api.example.com/search",
                parserClass = "com.app.parser.DefaultSearchParser",
                isEnabled = true,
                priority = 100
            ),
            Source(
                name = "Default Parser Source",
                type = SourceType.PARSER,
                baseUrl = "https://api.example.com/parser",
                parserClass = "com.app.parser.DefaultParser",
                isEnabled = true,
                priority = 90
            )
        )
        
        sourceRepository.insertSources(defaultSources)
    }
    
    suspend fun importSourcesFromJson(json: String) = withContext(Dispatchers.IO) {
        val gson = Gson()
        val listType = object : TypeToken<List<SourceJson>>() {}.type
        val sourcesJson: List<SourceJson> = gson.fromJson(json, listType)
        
        val sources = sourcesJson.map { sourceJson ->
            Source(
                name = sourceJson.name,
                type = SourceType.valueOf(sourceJson.type),
                baseUrl = sourceJson.baseUrl,
                parserClass = sourceJson.parserClass,
                isEnabled = sourceJson.isEnabled,
                priority = sourceJson.priority,
                metadata = sourceJson.metadata
            )
        }
        
        sourceRepository.insertSources(sources)
    }
}
