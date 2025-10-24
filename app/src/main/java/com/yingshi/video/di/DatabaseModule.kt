package com.yingshi.video.di

import android.content.Context
import androidx.room.Room
import com.yingshi.video.data.local.dao.CustomRuleDao
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.database.DatabaseMigrations
import com.yingshi.video.data.local.database.YingshiDatabase
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
    fun provideYingshiDatabase(@ApplicationContext context: Context): YingshiDatabase {
        return Room.databaseBuilder(
            context,
            YingshiDatabase::class.java,
            YingshiDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            // Uncomment to use migrations instead of destructive migration:
            // .addMigrations(*DatabaseMigrations.getAllMigrations())
            .build()
    }

    @Provides
    @Singleton
    fun provideSearchSourceDao(database: YingshiDatabase): SearchSourceDao {
        return database.searchSourceDao()
    }

    @Provides
    @Singleton
    fun provideParserSourceDao(database: YingshiDatabase): ParserSourceDao {
        return database.parserSourceDao()
    }

    @Provides
    @Singleton
    fun provideCustomRuleDao(database: YingshiDatabase): CustomRuleDao {
        return database.customRuleDao()
    }
}
