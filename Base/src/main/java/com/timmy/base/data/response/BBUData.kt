package com.timmy.base.data.response

import com.google.gson.annotations.SerializedName

class BBUData : ArrayList<BBUDataItem>()

data class BBUDataItem(
    @SerializedName("Code")
    var code: String? = "",
    @SerializedName("Date")
    var date: String? = "",
    @SerializedName("DividendYield")
    var dividendYield: String? = "",
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("PBratio")
    var pBratio: String? = "",
    @SerializedName("PEratio")
    var pEratio: String? = ""
)