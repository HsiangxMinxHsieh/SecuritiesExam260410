package com.timmy.assetslibs.repo

import android.content.Context
import com.timmymike.logtool.loge
import com.timmymike.logtool.toDataBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *    author: Timmy
 *    date  : 2023/07/31
 *    desc  : 讀取 assets 內 JSON，並可自動轉成指定型別。
 */

class AssetsRepository(val context: Context) {

    /**
     * 使用協程非同步讀取 Assets 並轉換成 Data Class
     */
    suspend inline fun <reified T : Any> getAssetsToClass(fileName: String): T = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open(fileName).bufferedReader().use { it.readText() }.toDataBean<T>()
        }.onFailure {
            loge("解析 Assets 失敗，檔案: $fileName", it)
        }.getOrNull() ?: T::class.java.getDeclaredConstructor().newInstance()
    }

    /**
     * 非同步讀取 Assets 字串
     */
    suspend fun getAssetsContent(fileName: String): String = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }.onFailure {
            loge("讀取 Assets 失敗，檔案: $fileName", it)
        }.getOrDefault("")
    }
}