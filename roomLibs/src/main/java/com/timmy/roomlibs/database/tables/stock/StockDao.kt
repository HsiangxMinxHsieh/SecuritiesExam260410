package com.timmy.roomlibs.database.tables.stock

import androidx.room.*
import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem

/**
 *     author: Timmy
 *     date  : 2026/04/10
 *     desc  : 界接資料的DAO
 */

@Dao
interface StockDao {

    @get:Query("SELECT * FROM StockEntity ORDER BY code ASC")
    val stockDataAsc: List<StockEntity>

    @get:Query("SELECT * FROM StockEntity ORDER BY code DESC")
    val stockDataDesc: List<StockEntity>

    @Upsert
    suspend fun upsertAll(list: List<StockEntity>)

    @Query("SELECT * FROM StockEntity WHERE code = :code")
    fun getStockByCode(code: String): StockEntity?

    @Update
    suspend fun update(stock: StockEntity)

    @Delete
    fun delete(data: StockEntity)

    @Query("DELETE FROM StockEntity")
    fun deleteAll()

}

/**
 * 處理 BBUData (本益比、殖利率)
 */
@Transaction
suspend fun StockDao.insertByBBU(bbuData: List<BBUDataItem>) {
    val list = bbuData.map {
        StockEntity(
            code = it.code.toString(),
            name = it.name.toString()
        )
    }
    upsertAll(list)
}
/**
 * 處理 StockAVGData (收盤價、月平均價)
 */
@Transaction
suspend fun StockDao.insertByStockAVG(stockAVGData: List<StockAVGDataItem>) {
    val list = stockAVGData.map {
        StockEntity(
            code = it.code.toString(),
            name = it.name.toString(),
            closingPrice = it.closingPrice.toString(),
            monthlyAveragePrice = it.monthlyAveragePrice.toString()
        )
    }
    upsertAll(list)
}

/**
 * 處理 StockData (詳細交易資訊、漲跌價差)
 */
@Transaction
suspend fun StockDao.insertByStock(stockData: List<StockDataItem>) {
    val list = stockData.map {
        StockEntity(
            code = it.code.toString(),
            name = it.name.toString(),
            tradeVolume = it.tradeVolume.toString(),
            tradeValue = it.tradeValue.toString(),
            openingPrice = it.openingPrice.toString(),
            highestPrice = it.highestPrice.toString(),
            lowestPrice = it.lowestPrice.toString(),
            closingPrice = it.closingPrice.toString(),
            change = it.change.toString(),
            transaction = it.transaction.toString()
        )
    }
    upsertAll(list)
}