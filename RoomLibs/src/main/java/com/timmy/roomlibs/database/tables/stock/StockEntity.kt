package com.timmy.roomlibs.database.tables.stock

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StockEntity(
    @PrimaryKey val code: String = "",       // 股票代號 (Code)
    val name: String = "",                   // 股票名稱 (Name)
    val openingPrice: Double = 0.0,          // 開盤價 (OpeningPrice)
    val highestPrice: Double = 0.0,          // 最高價 (HighestPrice)
    val lowestPrice: Double = 0.0,           // 最低價 (LowestPrice)
    val closingPrice: Double = 0.0,          // 收盤價 (ClosingPrice)
    val change: Double = 0.0,                // 漲跌價差 (Change)
    val transactionCount: Double = 0.0,      // 成交筆數 (Transaction)
    val tradeVolume: Double = 0.0,           // 成交股數 (TradeVolume)
    val tradeValue: Double = 0.0,            // 成交金額 (TradeValue)
    val monthlyAveragePrice: Double = 0.0,   // 月平均價 (MonthlyAveragePrice)
    val dividendYield: Double = 0.0,         // 殖利率(%)
    val pBratio: Double = 0.0,               // 股價淨值比
    val pEratio: Double = 0.0,               // 本益比
    // 以下是因為要優化UI效能所使用的，預先填入的顏色值
    val openingPriceColor: Int = 0,
    val closingPriceColor: Int = 0,
    val changeColor: Int = 0,

    )