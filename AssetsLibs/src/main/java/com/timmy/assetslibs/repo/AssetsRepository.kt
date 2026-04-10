package com.timmy.assetslibs.repo

import android.content.Context
import com.timmymike.logtool.loge
import com.timmymike.logtool.toDataBean

/**
 *    author: Timmy
 *    date  : 2023/07/31
 *    desc  : 讀取 assets 內 JSON，並可自動轉成指定型別。
 */

class AssetsRepository(val context: Context) {

    inline fun <reified T : Any> getAssetsToClass(fileName: String): T = kotlin.runCatching {
        context.assets.open(fileName).let { stream ->
            stream.bufferedReader().use { it.readText() }.run { this.toDataBean<T>() }
        }
    }.onFailure { loge("取內容時發生例外，錯誤內容=>", it) }.getOrNull() ?: T::class.java.newInstance()

    fun getAssetsContent(fileName: String): String = kotlin.runCatching {
        context.assets.open(fileName).let { stream ->
            stream.bufferedReader().use { it.readText() }.run { this }
        }
    }.onFailure { loge("取內容時發生例外，錯誤內容=>", it) }.getOrNull() ?: ""

}