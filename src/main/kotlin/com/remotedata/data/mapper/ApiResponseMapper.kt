package com.remotedata.data.mapper

import com.remotedata.data.remote.dto.ApiResponseDto
import com.remotedata.domain.entity.ApiResult

object ApiResponseMapper {
    
    fun ApiResponseDto.toDomain(): ApiResult {
        return ApiResult(
            id = this.id ?: "",
            status = this.status ?: 0,
            data = this.data,
            timestamp = this.timestamp ?: System.currentTimeMillis()
        )
    }
    
    fun List<ApiResponseDto>.toDomain(): List<ApiResult> {
        return map { it.toDomain() }
    }
}
