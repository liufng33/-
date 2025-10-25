package com.sourcemanager.di

import android.content.Context
import androidx.room.Room
import com.sourcemanager.data.local.dao.SourceDao
import com.sourcemanager.data.local.database.SourceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideSourceDatabase(
        @ApplicationContext context: Context
    ): SourceDatabase {
        return Room.databaseBuilder(
            context,
            SourceDatabase::class.java,
            "source_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSourceDao(database: SourceDatabase): SourceDao {
        return database.sourceDao()
    }
}
