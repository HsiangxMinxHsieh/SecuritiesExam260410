package com.timmy.datastorelibs.di

import com.timmy.base.inteface.DataStoreProvider
import com.timmy.datastorelibs.repo.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun provideTokenProvider(
        repository: DataStoreRepository
    ): DataStoreProvider {
        return DataStoreProvider(repository)
    }
}