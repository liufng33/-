package com.yingshi.video.di

import android.content.Context
import com.yingshi.video.data.local.dao.CustomRuleDao
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.dao.SearchSourceDao
import com.yingshi.video.data.local.preferences.PreferencesManager
import com.yingshi.video.data.local.seeding.DataSeeder
import com.yingshi.video.data.remote.ApiService
import com.yingshi.video.data.repository.PreferencesRepositoryImpl
import com.yingshi.video.data.repository.SourceRepositoryImpl
import com.yingshi.video.domain.repository.PreferencesRepository
import com.yingshi.video.domain.repository.SourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideSourceRepository(
        searchSourceDao: SearchSourceDao,
        parserSourceDao: ParserSourceDao,
        customRuleDao: CustomRuleDao,
        apiService: ApiService
    ): SourceRepository {
        return SourceRepositoryImpl(
            searchSourceDao = searchSourceDao,
            parserSourceDao = parserSourceDao,
            customRuleDao = customRuleDao,
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesManager: PreferencesManager
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideDataSeeder(
        @ApplicationContext context: Context,
        apiService: ApiService,
        searchSourceDao: SearchSourceDao,
        parserSourceDao: ParserSourceDao
    ): DataSeeder {
        return DataSeeder(
            context = context,
            apiService = apiService,
            searchSourceDao = searchSourceDao,
            parserSourceDao = parserSourceDao
        )
    }
}
