package com.cleanarch.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cleanarch.data.source.local.database.dao.SampleDao
import com.cleanarch.data.source.local.database.entity.SampleEntity

@Database(
    entities = [SampleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao
}
