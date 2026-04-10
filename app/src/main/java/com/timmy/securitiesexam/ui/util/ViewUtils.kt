package com.timmy.securitiesexam.ui.util

import android.view.Gravity
import android.widget.TextView
import com.timmy.securitiesexam.R
import com.timmymike.viewtool.getRoundBgById
import com.timmymike.viewtool.getScreenHeightPixels
import com.timmymike.viewtool.getScreenWidthPixels
import com.timmymike.viewtool.setClickBgState

fun TextView.setSelectStyle() {
    isClickable = true
    height = getScreenHeightPixels() / 12
    width = getScreenWidthPixels() / 2
    gravity = Gravity.CENTER
    background = setClickBgState(
        this.context.getRoundBgById(10, bgColorID = R.color.transparent, strokeColorID = R.color.white, strokeWidth = 1),
        this.context.getRoundBgById(10, bgColorID = R.color.DarkGray, strokeWidth = 1, strokeColorID = R.color.black),
    )
}

fun TextView.setSelectStyleNotBackGround(defaultColor: Int = R.color.red, pressColor: Int = R.color.DarkGray) {
    isClickable = true
    height = getScreenHeightPixels() / 12
    width = getScreenWidthPixels() / 2
    gravity = Gravity.CENTER
    background = setClickBgState(
        this.context.getRoundBgById(10, defaultColor),
        this.context.getRoundBgById(10, bgColorID = pressColor),
    )
}