package com.timmy.securitiesexam.ui.util

import android.app.Activity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.timmy.securitiesexam.R
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.getRoundBg

fun Activity.getStatusBarHeight(): Int {
    val windowInsets = ViewCompat.getRootWindowInsets(this.window.decorView)
    return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
}

fun Fragment.getSelectBack() = getRoundBg(5, getResourceColor(R.color.theme_light), getResourceColor(R.color.sort_select), 2)
fun Fragment.getUnSelectBack() = getRoundBg(5, getResourceColor(R.color.theme_light))
