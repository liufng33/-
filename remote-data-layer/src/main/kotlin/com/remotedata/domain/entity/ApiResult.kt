package com.remotedata.domain.entity

data class ApiResult(
    val id: String,
    val status: Int,
    val data: String?,
    val timestamp: Long = System.currentTimeMillis()
)
