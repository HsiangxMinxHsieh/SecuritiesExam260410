package com.timmy.roomlibs.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.timmy.roomlibs.database.tables.stock.StockDao
import com.timmy.roomlibs.database.tables.stock.StockEntity

/**
 *     author: Timmy
 *     date  : 2023/08/11
 *     desc  : Room 資料庫定義，供 Hilt 注入
 *
 */

@Database(
    entities = [StockEntity::class],
    version = UpdateExt.databaseVersion, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun stockDao(): StockDao
}
