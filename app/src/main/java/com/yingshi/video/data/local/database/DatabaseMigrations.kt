package com.yingshi.video.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Add a new column to search_sources table
            // database.execSQL("ALTER TABLE search_sources ADD COLUMN new_field TEXT")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Create a new table
            // database.execSQL("""
            //     CREATE TABLE IF NOT EXISTS favorite_videos (
            //         id TEXT NOT NULL PRIMARY KEY,
            //         video_id TEXT NOT NULL,
            //         timestamp INTEGER NOT NULL
            //     )
            // """.trimIndent())
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Add index for better query performance
            // database.execSQL("CREATE INDEX IF NOT EXISTS index_search_sources_priority ON search_sources(priority)")
        }
    }

    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4
        )
    }
}
