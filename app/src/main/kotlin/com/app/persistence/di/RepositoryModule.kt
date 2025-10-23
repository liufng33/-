package com.app.persistence.di

import com.app.persistence.data.repository.CustomRuleRepositoryImpl
import com.app.persistence.data.repository.PreferencesRepositoryImpl
import com.app.persistence.data.repository.SourceRepositoryImpl
import com.app.persistence.data.repository.UserSelectionRepositoryImpl
import com.app.persistence.domain.repository.CustomRuleRepository
import com.app.persistence.domain.repository.PreferencesRepository
import com.app.persistence.domain.repository.SourceRepository
import com.app.persistence.domain.repository.UserSelectionRepository
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
}
