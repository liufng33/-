package com.sourcemanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sourcemanager.data.local.dao.SourceDao
import com.sourcemanager.data.local.entity.SourceEntity

@Database(
    entities = [SourceEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SourceDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}
