package com.timmy.securitiesexam.ui.util

import android.graphics.Color
import android.widget.TextView
import androidx.core.graphics.toColorInt

//fun TextView.setSelectStyle() {
//    isClickable = true
//    height = getScreenHeightPixels() / 12
//    width = getScreenWidthPixels() / 2
//    gravity = Gravity.CENTER
//    background = setClickBgState(
//        this.context.getRoundBgById(10, bgColorID = R.color.transparent, strokeColorID = R.color.white, strokeWidth = 1),
//        this.context.getRoundBgById(10, bgColorID = R.color.DarkGray, strokeWidth = 1, strokeColorID = R.color.black),
//    )
//}

fun TextView.setTextColorByTarget(current: String?, target: String?) {
    this.setTextColor(getTextColor(current, target))
}

fun getTextColor(current: String?, target: String?): Int {
    val curVal = current?.toDoubleOrNull() ?: return Color.GRAY
    val tarVal = target?.toDoubleOrNull() ?: return Color.GRAY
    val diff = curVal - tarVal
    return when {
        diff > 0 -> Color.RED   // 漲 -> 紅
        diff < 0 -> "#4CAF50".toColorInt()
        else -> Color.GRAY
    }

}

//fun TextView.setSelectStyleNotBackGround(defaultColor: Int = R.color.red, pressColor: Int = R.color.DarkGray) {
//    isClickable = true
//    height = getScreenHeightPixels() / 12
//    width = getScreenWidthPixels() / 2
//    gravity = Gravity.CENTER
//    background = setClickBgState(
//        this.context.getRoundBgById(10, defaultColor),
//        this.context.getRoundBgById(10, bgColorID = pressColor),
//    )
//}