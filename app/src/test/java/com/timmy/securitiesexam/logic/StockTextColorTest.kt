package com.timmy.securitiesexam.logic

import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author timmy
 * 顏色邏輯測試：紅綠灰字判斷
 */
class StockTextColorTest {
    private val processor = AStockDataProcessor()

    // 模擬顏色常量（單元測試環境沒有 Android Color，直接用 Int 定義）
    val colorRise = 0xFFFF0000.toInt()
    val colorFall = 0xFF14bd43.toInt()
    val colorRemain = 0xFF808080.toInt()

    // -------------------------------------------------------------------------
    // 1. 紅字邏輯測試
    // -------------------------------------------------------------------------
    @Test
    fun `測試紅字判斷邏輯`() {
        // 假資料 (Fake API Responses)
        // 合併的股票是 "5566" // 收盤價 > 月平均價 ,漲跌價差 > 0=>紅字
        val stockCode = "5566"
        val stockName = "五五六六光電"

        // 第一份 API: BWIBBU_ALL (本益比、殖利率)
        val fakeBbu = listOf(
            BBUDataItem(code = stockCode, name = stockName, pEratio = "15.5", dividendYield = "3.5", pBratio = "5.0")
        )

        // 第二份 API: STOCK_DAY_AVG_ALL (收盤價、月均價)
        val fakeAvg = listOf(
            StockAVGDataItem(code = stockCode, name = stockName, closingPrice = "181.0", monthlyAveragePrice = "300.0")
        )

        // 第三份 API: STOCK_DAY_ALL (成交資訊)
        val fakeStock = listOf(
            StockDataItem(code = stockCode, name = stockName, openingPrice = "595.0", closingPrice = "515.0", change = "10.0")
        )

        val result = processor.merge(fakeBbu, fakeAvg, fakeStock).first()

        // 收盤價 > 月平均價 -> 紅字
        assertEquals(colorRise, processor.getPriceColor(result.closingPrice, result.monthlyAveragePrice))

        // 漲跌價差 > 0 -> 紅字
        assertEquals(colorRise, processor.getPriceColor(result.change, 0.0))
    }

    // -------------------------------------------------------------------------
    // 2. 綠字邏輯測試
    // -------------------------------------------------------------------------
    @Test
    fun `測試綠字判斷邏輯`() {
        // 假資料 (Fake API Responses)
        // 合併的股票是 "3345678" // 收盤價 < 月平均價 ,漲跌價差 < 0=>綠字
        val stockCode = "3345678"
        val stockName = "香港電話"

        // 第一份 API: BWIBBU_ALL (本益比、殖利率)
        val fakeBbu = listOf(
            BBUDataItem(code = stockCode, name = stockName, pEratio = "15.5", dividendYield = "3.5", pBratio = "5.0")
        )

        // 第二份 API: STOCK_DAY_AVG_ALL (收盤價、月均價)
        val fakeAvg = listOf(
            StockAVGDataItem(code = stockCode, name = stockName, closingPrice = "471.0", monthlyAveragePrice = "300.0")
        )

        // 第三份 API: STOCK_DAY_ALL (成交資訊)
        val fakeStock = listOf(
            StockDataItem(code = stockCode, name = stockName, openingPrice = "595.0", closingPrice = "224.0", change = "-12.0")
        )

        val result = processor.merge(fakeBbu, fakeAvg, fakeStock).first()


        // 收盤價 < 月平均價 -> 綠字
        assertEquals(colorFall, processor.getPriceColor(result.closingPrice, result.monthlyAveragePrice))

        // 漲跌價差 < 0 -> 綠字
        assertEquals(colorFall, processor.getPriceColor(result.change, 0.0))

    }

    // -------------------------------------------------------------------------
    // 3. 灰字邏輯測試
    // -------------------------------------------------------------------------
    @Test
    fun `測試灰字判斷邏輯`() {
        // 假資料 (Fake API Responses)
        // 合併的股票是 "0066" // 收盤價 = 月平均價 ,漲跌價差 = 0=>灰字
        val stockCode = "0066"
        val stockName = "零零落落"

        // 第一份 API: BWIBBU_ALL (本益比、殖利率)
        val fakeBbu = listOf(
            BBUDataItem(code = stockCode, name = stockName, pEratio = "15.5", dividendYield = "3.5", pBratio = "5.0")
        )

        // 第二份 API: STOCK_DAY_AVG_ALL (收盤價、月均價)
        val fakeAvg = listOf(
            StockAVGDataItem(code = stockCode, name = stockName, closingPrice = "218.0", monthlyAveragePrice = "381.0")
        )

        // 第三份 API: STOCK_DAY_ALL (成交資訊)
        val fakeStock = listOf(
            StockDataItem(code = stockCode, name = stockName, openingPrice = "595.0", closingPrice = "381.0", change = "0.0")
        )

        val result = processor.merge(fakeBbu, fakeAvg, fakeStock).first()

        // 收盤價 = 月平均價 -> 灰字
        assertEquals(colorRemain, processor.getPriceColor(result.closingPrice, result.monthlyAveragePrice))

        // 漲跌價差 = 0 -> 灰字
        assertEquals(colorRemain, processor.getPriceColor(result.change, 0.0))
    }

}