package com.timmy.roomlibs.database.tables.stock

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

/**
 *     author: Timmy
 *     date  : 2026/04/10
 *     desc  : 界接資料的DAO
 */

@Dao
interface StockDao {

    @get:Query("SELECT * FROM StockEntity ORDER BY code ASC")
    val stockDataAsc: List<StockEntity>

    @Query("SELECT * FROM StockEntity ORDER BY code DESC")
    suspend fun getStockDataDesc(): List<StockEntity>

    @Query("SELECT COUNT(*) FROM StockEntity")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertAll(list: List<StockEntity>)

    @Delete
    fun delete(data: StockEntity)

    @Query("DELETE FROM StockEntity")
    fun deleteAll()

}
