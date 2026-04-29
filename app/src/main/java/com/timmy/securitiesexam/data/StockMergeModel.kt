package com.timmy.securitiesexam.data

import com.timmy.base.cons.GlobalConst

private const val defaultValue = GlobalConst.EMPTY_DATA_VALUE

data class StockMergeModel(
    val code: String,
    var name: String = "",
    var openingPrice: Double = defaultValue,
    var highestPrice: Double = defaultValue,
    var lowestPrice: Double = defaultValue,
    var closingPrice: Double = defaultValue,
    var change: Double = defaultValue,
    var transaction: Double = defaultValue,
    var tradeVolume: Double = defaultValue,
    var tradeValue: Double = defaultValue,
    var monthlyAveragePrice: Double = defaultValue,
    var dividendYield: Double = defaultValue,
    var pBratio: Double = defaultValue,
    var pEratio: Double = defaultValue
)