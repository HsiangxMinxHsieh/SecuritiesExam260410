package com.timmy.roomlibs.repo

import com.timmy.roomlibs.database.tables.stock.StockDao
import com.timmy.roomlibs.database.tables.stock.StockEntity
import javax.inject.Inject

/**
 *     author: Timmy
 *     date  : 2023/08/09
 *     desc  : Room 資料同步讀寫
 */
class RoomRepository @Inject constructor(
    private val stockDao: StockDao
) {
    companion object {
        private const val LIMIT = 100
    }

    suspend fun getCount() = stockDao.getCount()

    suspend fun getDataDesc(offset: Int) = stockDao.getStockDataDesc(LIMIT, offset = offset)

    suspend fun getDataAsc(offset: Int) = stockDao.getStockDataAsc(LIMIT, offset = offset)

    suspend fun upsertAll(list: List<StockEntity>) {
        stockDao.upsertAll(list)
    }
}