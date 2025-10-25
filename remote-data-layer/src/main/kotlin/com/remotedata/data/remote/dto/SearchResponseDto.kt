package com.remotedata.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("results")
    val results: List<SearchItemDto>? = null,
    
    @SerializedName("total")
    val total: Int? = null,
    
    @SerializedName("page")
    val page: Int? = null
)

data class SearchItemDto(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("title")
    val title: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("url")
    val url: String? = null,
    
    @SerializedName("score")
    val score: Double? = null
)
