package com.timmy.base.data.response

import com.google.gson.annotations.SerializedName

class StockData : ArrayList<StockDataItem>()

data class StockDataItem(
    @SerializedName("Change")
    var change: String? = "",
    @SerializedName("ClosingPrice")
    var closingPrice: String? = "",
    @SerializedName("Code")
    var code: String? = "",
    @SerializedName("Date")
    var date: String? = "",
    @SerializedName("HighestPrice")
    var highestPrice: String? = "",
    @SerializedName("LowestPrice")
    var lowestPrice: String? = "",
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("OpeningPrice")
    var openingPrice: String? = "",
    @SerializedName("TradeValue")
    var tradeValue: String? = "",
    @SerializedName("TradeVolume")
    var tradeVolume: String? = "",
    @SerializedName("Transaction")
    var transaction: String? = ""
)