package com.app.persistence.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Future migration logic goes here
            // Example: Adding a new column
            // database.execSQL("ALTER TABLE sources ADD COLUMN new_field TEXT")
        }
    }
    
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            // Add migrations as they are created
            // MIGRATION_1_2
        )
    }
}
