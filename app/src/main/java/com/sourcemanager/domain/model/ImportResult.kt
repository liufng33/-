package com.sourcemanager.domain.model

sealed class ImportResult {
    data class Success(val importedCount: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
    data class Progress(val current: Int, val total: Int) : ImportResult()
}
