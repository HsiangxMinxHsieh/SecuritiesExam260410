package com.timmy.securitiesexam.logic

import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem
import org.junit.Assert.assertEquals
import org.junit.Test

class StockDataMergeTest {
    private val processor = AStockDataProcessor()

    @Test
    fun `測試三份API資料正確合併成同一筆股票資料`() {
        // 1. 假資料 (Fake API Responses)
        // 合併的股票是 "5566"
        val stockCode = "5566"
        val stockName = "五五六六光電"

        // 第一份 API: BWIBBU_ALL (本益比、殖利率)
        val fakeBbu = listOf(
            BBUDataItem(code = stockCode, name = stockName, pEratio = "15.5", dividendYield = "3.5", pBratio = "5.0")
        )

        // 第二份 API: STOCK_DAY_AVG_ALL (收盤價、月均價)
        val fakeAvg = listOf(
            StockAVGDataItem(code = stockCode, name = stockName, closingPrice = "871.0", monthlyAveragePrice = "590.0")
        )

        // 第三份 API: STOCK_DAY_ALL (成交資訊)
        val fakeStock = listOf(
            StockDataItem(code = stockCode, name = stockName, openingPrice = "595.0", closingPrice = "871.0", change = "5.0")
        )

        // 2. 執行合併
        val result = processor.merge(fakeBbu, fakeAvg, fakeStock)

        // 3. 驗證結果 (Assertion)
        // 確認只生成了一筆資料
        assertEquals(1, result.size)

        val mergedEntity = result[0]
        assertEquals(stockCode, mergedEntity.code)

        // 驗證來自 BBU 的資料
        assertEquals(15.5, mergedEntity.pEratio, 0.0)
        assertEquals(3.5, mergedEntity.dividendYield, 0.0)

        // 驗證來自 AVG 的資料
        assertEquals(590.0, mergedEntity.monthlyAveragePrice, 0.0)

        // 驗證來自 STOCK 的資料
        assertEquals(871.0, mergedEntity.closingPrice, 0.0)
    }

}