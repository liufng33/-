package com.app.persistence.di

import android.content.Context
import androidx.room.Room
import com.app.persistence.data.local.database.AppDatabase
import com.app.persistence.data.local.database.dao.CustomRuleDao
import com.app.persistence.data.local.database.dao.SourceDao
import com.app.persistence.data.local.database.dao.UserSelectionDao
import com.app.persistence.data.local.database.migration.DatabaseMigrations
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
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(*DatabaseMigrations.getAllMigrations())
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSourceDao(database: AppDatabase): SourceDao {
        return database.sourceDao()
    }
    
    @Provides
    @Singleton
    fun provideCustomRuleDao(database: AppDatabase): CustomRuleDao {
        return database.customRuleDao()
    }
    
    @Provides
    @Singleton
    fun provideUserSelectionDao(database: AppDatabase): UserSelectionDao {
        return database.userSelectionDao()
    }
}
