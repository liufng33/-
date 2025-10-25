package com.app.persistence.di

import com.app.persistence.data.repository.*
import com.app.persistence.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindSourceRepository(
        impl: SourceRepositoryImpl
    ): SourceRepository
    
    @Binds
    @Singleton
    abstract fun bindCustomRuleRepository(
        impl: CustomRuleRepositoryImpl
    ): CustomRuleRepository
    
    @Binds
    @Singleton
    abstract fun bindUserSelectionRepository(
        impl: UserSelectionRepositoryImpl
    ): UserSelectionRepository
    
    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
    
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository
    
    @Binds
    @Singleton
    abstract fun bindParserRepository(
        impl: ParserRepositoryImpl
    ): ParserRepository
    
    @Binds
    @Singleton
    abstract fun bindPlaybackRepository(
        impl: PlaybackRepositoryImpl
    ): PlaybackRepository
}
