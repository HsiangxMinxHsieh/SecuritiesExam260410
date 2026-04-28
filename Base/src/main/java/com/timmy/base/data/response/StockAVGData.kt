package com.timmy.base.data.response

import com.google.gson.annotations.SerializedName

class StockAVGData : ArrayList<StockAVGDataItem>()

data class StockAVGDataItem(
    @SerializedName("ClosingPrice")
    var closingPrice: String? = "",
    @SerializedName("Code")
    var code: String? = "",
    @SerializedName("Date")
    var date: String? = "",
    @SerializedName("MonthlyAveragePrice")
    var monthlyAveragePrice: String? = "",
    @SerializedName("Name")
    var name: String? = ""
)