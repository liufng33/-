package com.yingshi.video.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yingshi.video.data.local.dao.CustomRuleDao
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.entity.CustomRuleEntity
import com.yingshi.video.data.local.entity.ParserSourceEntity
import com.yingshi.video.data.local.entity.SearchSourceEntity

@Database(
    entities = [
        SearchSourceEntity::class,
        ParserSourceEntity::class,
        CustomRuleEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class YingshiDatabase : RoomDatabase() {
    abstract fun searchSourceDao(): SearchSourceDao
    abstract fun parserSourceDao(): ParserSourceDao
    abstract fun customRuleDao(): CustomRuleDao

    companion object {
        const val DATABASE_NAME = "yingshi_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example migration for future use
                // database.execSQL("ALTER TABLE search_sources ADD COLUMN new_column TEXT")
            }
        }
    }
}
