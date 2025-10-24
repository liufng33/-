package com.yingshi.video.data.local.seeding

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.yingshi.video.data.remote.SourcesResponse

object SourceJsonParser {
    private val gson = Gson()

    fun parseSourcesJson(jsonString: String): SourcesResponse? {
        return try {
            gson.fromJson(jsonString, SourcesResponse::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun toJson(sourcesResponse: SourcesResponse): String {
        return gson.toJson(sourcesResponse)
    }

    fun isValidSourcesJson(jsonString: String): Boolean {
        return try {
            val response = parseSourcesJson(jsonString)
            response != null && (
                response.searchSources?.isNotEmpty() == true ||
                response.parserSources?.isNotEmpty() == true
            )
        } catch (e: Exception) {
            false
        }
    }
}
