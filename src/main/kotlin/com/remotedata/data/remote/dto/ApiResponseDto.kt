package com.remotedata.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponseDto(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("status")
    val status: Int? = null,
    
    @SerializedName("data")
    val data: String? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("timestamp")
    val timestamp: Long? = null
)
