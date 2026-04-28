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

    @Query("SELECT * FROM StockEntity ORDER BY code Asc LIMIT :limit OFFSET :offset")
    suspend fun getStockDataAsc(limit: Int, offset: Int): List<StockEntity>

    @Query("SELECT * FROM StockEntity ORDER BY code DESC LIMIT :limit OFFSET :offset ")
    suspend fun getStockDataDesc(limit: Int, offset: Int): List<StockEntity>

    @Query("SELECT COUNT(*) FROM StockEntity")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertAll(list: List<StockEntity>)

    @Delete
    fun delete(data: StockEntity)

    @Query("DELETE FROM StockEntity")
    fun deleteAll()

}
