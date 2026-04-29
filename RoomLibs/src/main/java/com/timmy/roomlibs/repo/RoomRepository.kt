package com.timmy.roomlibs.repo

import androidx.sqlite.db.SimpleSQLiteQuery
import com.timmy.base.cons.GlobalConst
import com.timmy.roomlibs.database.tables.stock.StockDao
import com.timmy.roomlibs.database.tables.stock.StockEntity
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

/**
 *     author: Timmy
 *     date  : 2023/08/09
 *     desc  : Room 資料同步讀寫
 */
class RoomRepository @Inject constructor(
    private val stockDao: StockDao
) {
    companion object {
        const val LIMIT = 100
    }

    // 自動抓取 StockEntity 內所有定義過的屬性名稱
    private val validColumns: Set<String> = StockEntity::class.memberProperties.map { it.name }.toSet()

    suspend fun getSortedStocks(column: String, isAscending: Boolean, offset: Int = 0): List<StockEntity> {
        // 安全檢查：如果傳入的欄位不在白名單內，強制使用預設值 "code"
        val safeColumn = if (validColumns.contains(column)) column else "code"
        val order = if (isAscending) "ASC" else "DESC"
        val emptyValue = GlobalConst.EMPTY_DATA_VALUE
        // 建立動態 SQL 語句
        val queryString = """
    SELECT * FROM StockEntity 
    WHERE $safeColumn != ${GlobalConst.EMPTY_DATA_VALUE} 
    ORDER BY $safeColumn $order 
    LIMIT $LIMIT OFFSET $offset
""".trimIndent()
        val query = SimpleSQLiteQuery(queryString)

        return stockDao.getStocksRaw(query)
    }

    suspend fun getCount() = stockDao.getCount()

    suspend fun upsertAll(list: List<StockEntity>) {
        stockDao.upsertAll(list)
    }
}