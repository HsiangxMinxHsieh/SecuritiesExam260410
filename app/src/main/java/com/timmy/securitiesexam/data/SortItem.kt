package com.timmy.securitiesexam.data

import com.google.gson.annotations.SerializedName

data class SortItem(
    @SerializedName("sortName")
    var sortName: String? = "",
    @SerializedName("sortOption")
    var sortOption: String? = ""
)

