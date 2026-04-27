package com.timmy.securitiesexam.ui.util

import android.graphics.Color
import android.widget.TextView
import com.timmy.securitiesexam.R
import com.timmymike.viewtool.getResourceColor


fun TextView.setTextColorByTarget(current: String?, target: String?) {
    val curVal = current?.toDoubleOrNull()
    val tarVal = target?.toDoubleOrNull()

    this.setTextColor(
        when {
            curVal == null || tarVal == null -> Color.GRAY
            curVal > tarVal -> this.context.getResourceColor(R.color.rise) // 漲
            curVal < tarVal -> this.context.getResourceColor(R.color.fall) // 跌
            else -> this.context.getResourceColor(R.color.remain)            // 持平
        }
    )
}
