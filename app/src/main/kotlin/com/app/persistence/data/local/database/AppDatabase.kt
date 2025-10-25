package com.app.persistence.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.persistence.data.local.database.dao.CustomRuleDao
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.local.database.dao.UserSelectionDao
import com.app.persistence.data.local.database.entity.CustomRuleEntity
import com.app.persistence.data.local.database.entity.SourceEntity
import com.app.persistence.data.local.database.entity.UserSelectionEntity

@Database(
    entities = [
        SourceEntity::class,
        CustomRuleEntity::class,
        UserSelectionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun customRuleDao(): CustomRuleDao
    abstract fun userSelectionDao(): UserSelectionDao
    
    companion object {
        const val DATABASE_NAME = "app_database.db"
    }
}
