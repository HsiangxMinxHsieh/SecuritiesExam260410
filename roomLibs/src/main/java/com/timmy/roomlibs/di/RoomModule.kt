package com.timmy.roomlibs.di

import android.app.Application
import androidx.room.Room
import com.timmy.roomlibs.database.AppDataBase
import com.timmy.roomlibs.database.tables.stock.StockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *
 *     author: Timmy
 *     date  : 2023/08/11
 *     desc  : Hilt依賴注入，用於提供AppDataBase和資料存取介面Dao
 *
 */

@Module
@InstallIn(SingletonComponent::class)
// 使用 SingletonComponent，RoomModule 綁定於 Application 生命週期。
object RoomModule {

    /**
     * @Provides：標註於 @Module 類別內，用於提供相依物件的方法。
     * @Singleton：提供單例。
     */
    @Provides
    @Singleton
    fun provideAppDataBase(application: Application): AppDataBase {
        return Room
            .databaseBuilder(application, AppDataBase::class.java, "SecuritiesExam.db")
//            .addMigrations(*UpdateExt.availableMigration) // 容許已上線的資料庫的更新 // 注意，若資料表有新增欄位，必須要可以為null。
            .fallbackToDestructiveMigration() // 破壞性遷移：變更 DB 版本時會清空並重建；若改用手動 addMigrations 可移除此行
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideStockDao(appDatabase: AppDataBase): StockDao {
        return appDatabase.stockDao()
    }

}
