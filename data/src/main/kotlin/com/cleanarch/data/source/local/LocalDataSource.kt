package com.cleanarch.data.source.local

import com.cleanarch.data.source.local.database.AppDatabase
import com.cleanarch.data.source.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val database: AppDatabase,
    private val preferencesManager: PreferencesManager
) {
    suspend fun saveData(data: String) {
        preferencesManager.saveString("sample_key", data)
    }
    
    fun observeData(): Flow<String> {
        return preferencesManager.getString("sample_key", "default_value")
    }
}
