package com.timmy.roomlibs.repo

import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem
import com.timmy.roomlibs.database.tables.stock.StockDao
import com.timmy.roomlibs.database.tables.stock.insertByBBU
import com.timmy.roomlibs.database.tables.stock.insertByStock
import com.timmy.roomlibs.database.tables.stock.insertByStockAVG
import javax.inject.Inject

/**
 *     author: Timmy
 *     date  : 2023/08/09
 *     desc  : Room 資料同步讀寫
 */
class RoomRepository @Inject constructor(
    private val stockDao: StockDao
) {
    suspend fun getCount() = stockDao.getCount()

    suspend fun getDataDesc() = stockDao.getStockDataDesc()

    suspend fun getDataAsc() = stockDao.stockDataAsc

    suspend fun insertByBBU(bbuData: List<BBUDataItem>) {
        stockDao.insertByBBU(bbuData)
    }

    suspend fun insertByStockAVG(stockAVGData: List<StockAVGDataItem>) {
        stockDao.insertByStockAVG(stockAVGData)
    }

    suspend fun insertByStock(stockData: List<StockDataItem>) {
        stockDao.insertByStock(stockData)
    }

}