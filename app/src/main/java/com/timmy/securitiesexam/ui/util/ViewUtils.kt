package com.timmy.securitiesexam.ui.util

import android.app.Activity
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.timmy.securitiesexam.R
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.getRoundBg
import com.timmymike.viewtool.setRippleBackground

fun Activity.getStatusBarHeight(): Int {
    val windowInsets = ViewCompat.getRootWindowInsets(this.window.decorView)
    return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
}

fun TextView.setRipple() = this.setRippleBackground(this.context.getResourceColor(R.color.ripple))

fun Activity.getSelectBack() = getRoundBg(5, getResourceColor(R.color.dialog_back), getResourceColor(R.color.sort_select), 2)
fun Activity.getUnSelectBack() = getRoundBg(5, getResourceColor(R.color.dialog_back))
