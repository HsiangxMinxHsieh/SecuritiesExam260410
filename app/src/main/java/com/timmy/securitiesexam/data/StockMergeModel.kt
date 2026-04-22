package com.timmy.securitiesexam.data

data class StockMergeModel(
    val code: String,
    var name: String = "",
    var openingPrice: String = "",
    var highestPrice: String = "",
    var lowestPrice: String = "",
    var closingPrice: String = "",
    var change: String = "",
    var transaction: String = "",
    var tradeVolume: String = "",
    var tradeValue: String = "",
    var monthlyAveragePrice: String = "",
    var dividendYield: String = "",
    var pBratio: String = "",
    var pEratio: String = ""
)