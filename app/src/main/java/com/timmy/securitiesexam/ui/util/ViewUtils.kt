package com.timmy.securitiesexam.ui.util

import android.app.Activity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Activity.getStatusBarHeight(): Int {
    val windowInsets = ViewCompat.getRootWindowInsets(this.window.decorView)
    return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
}
