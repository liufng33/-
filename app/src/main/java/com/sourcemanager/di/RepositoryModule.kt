package com.sourcemanager.di

import com.sourcemanager.data.local.dao.SourceDao
import com.sourcemanager.data.remote.api.SourceApiService
import com.sourcemanager.data.repository.SourceRepositoryImpl
import com.sourcemanager.domain.repository.SourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideSourceRepository(
        sourceDao: SourceDao,
        apiService: SourceApiService
    ): SourceRepository {
        return SourceRepositoryImpl(sourceDao, apiService)
    }
}
