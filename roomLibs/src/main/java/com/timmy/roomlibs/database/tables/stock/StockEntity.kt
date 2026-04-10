package com.timmy.roomlibs.database.tables.stock

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StockEntity(
    @PrimaryKey val code: String = "",       // 股票代號 (Code)
    val name: String = "",                   // 股票名稱 (Name)
    val openingPrice: String = "",           // 開盤價 (OpeningPrice)
    val highestPrice: String = "",           // 最高價 (HighestPrice)
    val lowestPrice: String = "",            // 最低價 (LowestPrice)
    val closingPrice: String = "",           // 收盤價 (ClosingPrice)
    val change: String = "",                 // 漲跌價差 (Change)
    val transaction: String = "",            // 成交筆數 (Transaction)
    val tradeVolume: String = "",            // 成交股數 (TradeVolume)
    val tradeValue: String = "",             // 成交金額 (TradeValue)
    val monthlyAveragePrice: String = ""     // 月平均價 (MonthlyAveragePrice)
)