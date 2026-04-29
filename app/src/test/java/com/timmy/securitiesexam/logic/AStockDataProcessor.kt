package com.timmy.securitiesexam.logic

import com.timmy.base.cons.GlobalConst
import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.securitiesexam.data.StockMergeModel

class AStockDataProcessor {

    fun merge(
        bbu: List<BBUDataItem>,
        avg: List<StockAVGDataItem>,
        stock: List<StockDataItem>
    ): List<StockEntity> {
        val map = mutableMapOf<String, StockMergeModel>()

        fun getOrCreate(code: String): StockMergeModel {
            return map.getOrPut(code) { StockMergeModel(code) }
        }

        // BBU
        bbu.forEach {
            val item = getOrCreate(it.code.orEmpty())
            item.name = it.name.orEmpty()
            item.dividendYield = it.dividendYield.toSafeDouble()
            item.pBratio = it.pBratio.toSafeDouble()
            item.pEratio = it.pEratio.toSafeDouble()
        }

        // AVG
        avg.forEach {
            val item = getOrCreate(it.code.orEmpty())
            item.name = it.name.orEmpty()
            item.closingPrice = it.closingPrice.toSafeDouble()
            item.monthlyAveragePrice = it.monthlyAveragePrice.toSafeDouble()
        }

        // STOCK
        stock.forEach {
            val item = getOrCreate(it.code.orEmpty())
            item.name = it.name.orEmpty()
            item.tradeVolume = it.tradeVolume.toSafeDouble()
            item.tradeValue = it.tradeValue.toSafeDouble()
            item.openingPrice = it.openingPrice.toSafeDouble()
            item.highestPrice = it.highestPrice.toSafeDouble()
            item.lowestPrice = it.lowestPrice.toSafeDouble()
            item.closingPrice = it.closingPrice.toSafeDouble()
            item.change = it.change.toSafeDouble()
            item.transaction = it.transaction.toSafeDouble()
        }

        // 內部方法：判斷資料大小與顏色
        fun getColor(current: Double, target: Double): Int {

            return when {
                current > target -> colorRise
                current < target -> colorFall
                else -> colorRemain
            }
        }

        return map.values.map {
            StockEntity(
                code = it.code,
                name = it.name,
                openingPrice = it.openingPrice,
                highestPrice = it.highestPrice,
                lowestPrice = it.lowestPrice,
                closingPrice = it.closingPrice,
                change = it.change,
                transactionCount = it.transaction,
                tradeVolume = it.tradeVolume,
                tradeValue = it.tradeValue,
                monthlyAveragePrice = it.monthlyAveragePrice,
                dividendYield = it.dividendYield,
                pBratio = it.pBratio,
                pEratio = it.pEratio,
                openingPriceColor = getColor(it.openingPrice, it.monthlyAveragePrice),
                closingPriceColor = getColor(it.closingPrice, it.monthlyAveragePrice), // 收盤價高於月平均價請用紅字,低於請用綠字顯示
                changeColor = getColor(it.change, 0.0) // 	漲跌價差 正的請用紅字,負的請用綠字
            )
        }
    }

    fun String?.toSafeDouble(): Double {
        return this
            ?.replace(",", "")
            ?.trim()
            ?.takeIf { it.isNotEmpty() && it != "--" && it != "-" }
            ?.toDoubleOrNull() ?: GlobalConst.EMPTY_DATA_VALUE
    }

    // 模擬顏色常量（單元測試環境沒有 Android Color，直接用 Int 定義）
    val colorRise = 0xFFFF0000.toInt()
    val colorFall = 0xFF14bd43.toInt()
    val colorRemain = 0xFF808080.toInt()

    // 模擬你 App 內的紅綠字判斷邏輯
    fun getPriceColor(current: Double, target: Double): Int {
        return when {
            current > target -> colorRise
            current < target -> colorFall
            else -> colorRemain
        }
    }
}