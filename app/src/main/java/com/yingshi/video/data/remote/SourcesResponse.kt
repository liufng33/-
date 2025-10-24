package com.yingshi.video.data.remote

import com.google.gson.annotations.SerializedName

data class SourcesResponse(
    @SerializedName("search_sources")
    val searchSources: List<RemoteSearchSource>? = null,
    
    @SerializedName("parser_sources")
    val parserSources: List<RemoteParserSource>? = null
)

data class RemoteSearchSource(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("api")
    val apiEndpoint: String,
    
    @SerializedName("enabled")
    val isEnabled: Boolean? = true,
    
    @SerializedName("priority")
    val priority: Int? = 0,
    
    @SerializedName("headers")
    val headers: Map<String, String>? = null,
    
    @SerializedName("description")
    val description: String? = null
)

data class RemoteParserSource(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val parserUrl: String,
    
    @SerializedName("domains")
    val supportedDomains: List<String>,
    
    @SerializedName("enabled")
    val isEnabled: Boolean? = true,
    
    @SerializedName("priority")
    val priority: Int? = 0,
    
    @SerializedName("timeout")
    val timeout: Long? = 30000,
    
    @SerializedName("headers")
    val headers: Map<String, String>? = null,
    
    @SerializedName("description")
    val description: String? = null
)
