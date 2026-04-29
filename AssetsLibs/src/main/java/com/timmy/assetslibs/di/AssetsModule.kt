package com.timmy.assetslibs.di

import android.content.Context
import com.timmy.assetslibs.repo.AssetsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *    author: Timmy
 *    date  : 2023/07/31
 *    desc  : 提供 AssetsRepository 的 Hilt 模組
 */

@Module
@InstallIn(SingletonComponent::class)
class AssetsModule {

    @Provides
    @Singleton
    fun provideAssetsRepository(@ApplicationContext ctx: Context): AssetsRepository {
        return AssetsRepository(ctx)
    }

}