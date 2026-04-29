package com.timmy.securitiesexam.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmy.assetslibs.repo.GetAPIRepository
import com.timmy.base.baseResponse.ResultState
import com.timmy.base.cons.GlobalConst
import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem
import com.timmy.datastorelibs.repo.DataStoreRepository
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.roomlibs.repo.RoomRepository
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.application.App
import com.timmy.securitiesexam.data.StockMergeModel
import com.timmymike.logtool.loge
import com.timmymike.timetool.TimeUnits
import com.timmymike.timetool.nowTime
import com.timmymike.viewtool.getResourceColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author timmy
 *
 * [SplashViewModel] 啟動頁面業務邏輯處理中心。
 *
 * 主要功能：
 * 1. 管理啟動生命週期狀態機 (SplashUiState)。
 * 2. 協調非同步網路請求與資料庫寫入作業。
 * 3. 計算並回傳下載進度百分比，驅動 UI 進度條更新。
 * 4. 決策導航權限 (canNavigate)，整合計時器與 API 回傳狀態以觸發跳頁。
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val roomRepo: RoomRepository,
    private val apiRepo: GetAPIRepository,
    private val dsRepo: DataStoreRepository
) : ViewModel() {

    companion object {
        private const val API_PROGRESS_WEIGHT = 0.3f // API 部分的權重
        private const val DB_PROGRESS_WEIGHT = 0.7f  // 資料寫入部分的權重
        private const val CHUNK_SIZE = 500           // 資料區間 // 此值越小會越頻繁呼叫Splash頁面更新畫面
        private const val GET_DATA_INTERVAL = TimeUnits.oneMin * 10 // 取資料的時間間隔 // 若上一次取完沒有大於這個時間，就不會再次取資料
    }

    // Splash的狀態。
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    // 避免螢幕轉向時重複啟動的Job
    private var getDataJob: Job? = null

    fun start() {
        if (getDataJob?.isActive == true) { // 螢幕轉向時，不重複啟動
            return
        }

        if (!shouldFetchData()) {
            completeData()
            return
        }

        getDataJob = viewModelScope.launch {
            runCatching {
                val apiData = fetchAllApiData()
                insertAllData(apiData)
            }.onSuccess {
                //寫入完畢才要更新現在時間。
                dsRepo.getDataInterval = nowTime
                completeData()
            }.onFailure { e ->
                handleError(e)
            }
        }
    }

    // Splash頁面的等待秒數結束
    fun onSplashTimerFinished() {
        _uiState.update { it.copy(timerFinished = true) }
    }

    // SplashViewModel的資料處理結束
    private fun completeData() {
        _uiState.update { it.copy(dataFinished = true) }
    }

    // 判斷是否需要取得資料
    private fun shouldFetchData(): Boolean {
        return dsRepo.getDataInterval + GET_DATA_INTERVAL < nowTime
    }

    private suspend fun fetchAllApiData(): Triple<List<BBUDataItem>, List<StockAVGDataItem>, List<StockDataItem>> =
        coroutineScope {
            val bbuDeferred = async { apiRepo.getBBUData().getOrThrow() }
            val avgDeferred = async { apiRepo.getStockAVG().getOrThrow() }
            val stockDeferred = async { apiRepo.getStock().getOrThrow() }

            updateProgress()

            Triple(
                bbuDeferred.await(),
                avgDeferred.await(),
                stockDeferred.await()
            )
        }

    // UnitTest
    fun String?.toSafeDouble(): Double {
        return this
            ?.replace(",", "")
            ?.trim()
            ?.takeIf { it.isNotEmpty() && it != "--" && it != "-" }
            ?.toDoubleOrNull() ?: GlobalConst.EMPTY_DATA_VALUE
    }

    val context: Context by lazy { App.instance.applicationContext }
    val colorRise: Int by lazy { context.getResourceColor(R.color.rise) }
    val colorFall: Int by lazy { context.getResourceColor(R.color.fall) }
    val colorRemain: Int by lazy { context.getResourceColor(R.color.remain) }

    // UnitTest
    private fun mergeData(
        bbu: List<BBUDataItem>,
        avg: List<StockAVGDataItem>,
        stock: List<StockDataItem>
    ): List<StockEntity> {

        val map = mutableMapOf<String, StockMergeModel>()

        fun getOrCreate(code: String): StockMergeModel {
            return map.getOrPut(code) { StockMergeModel(code) }
        }

        // BBU
        bbu.forEach {
            val item = getOrCreate(it.code.orEmpty())
            item.name = it.name.orEmpty()
            item.dividendYield = it.dividendYield.toSafeDouble()
            item.pBratio = it.pBratio.toSafeDouble()
            item.pEratio = it.pEratio.toSafeDouble()
        }

        // AVG
        avg.forEach {
            val item = getOrCreate(it.code.orEmpty())
            item.name = it.name.orEmpty()
            item.closingPrice = it.closingPrice.toSafeDouble()
            item.monthlyAveragePrice = it.monthlyAveragePrice.toSafeDouble()
        }

        // STOCK
        stock.forEach {
            val item = getOrCreate(it.code.orEmpty())
            item.name = it.name.orEmpty()
            item.tradeVolume = it.tradeVolume.toSafeDouble()
            item.tradeValue = it.tradeValue.toSafeDouble()
            item.openingPrice = it.openingPrice.toSafeDouble()
            item.highestPrice = it.highestPrice.toSafeDouble()
            item.lowestPrice = it.lowestPrice.toSafeDouble()
            item.closingPrice = it.closingPrice.toSafeDouble()
            item.change = it.change.toSafeDouble()
            item.transaction = it.transaction.toSafeDouble()
        }

        // 內部方法：判斷資料大小與顏色 // UnitTest
        fun getPriceColor(current: Double, target: Double): Int {
            return when {
                current > target -> colorRise
                current < target -> colorFall
                else -> colorRemain
            }
        }

        return map.values.map {
            StockEntity(
                code = it.code,
                name = it.name,
                openingPrice = it.openingPrice,
                highestPrice = it.highestPrice,
                lowestPrice = it.lowestPrice,
                closingPrice = it.closingPrice,
                change = it.change,
                transactionCount = it.transaction,
                tradeVolume = it.tradeVolume,
                tradeValue = it.tradeValue,
                monthlyAveragePrice = it.monthlyAveragePrice,
                dividendYield = it.dividendYield,
                pBratio = it.pBratio,
                pEratio = it.pEratio,
                openingPriceColor = getPriceColor(it.openingPrice, it.monthlyAveragePrice),
                closingPriceColor = getPriceColor(it.closingPrice, it.monthlyAveragePrice), // 收盤價高於月平均價請用紅字,低於請用綠字顯示
                changeColor = getPriceColor(it.change, 0.0) // 	漲跌價差 正的請用紅字,負的請用綠字
            )
        }
    }

    private suspend fun insertAllData(data: Triple<List<BBUDataItem>, List<StockAVGDataItem>, List<StockDataItem>>) {
        val (bbu, avg, stock) = data

        val merged = mergeData(bbu, avg, stock)

        val total = merged.size
        var inserted = 0

        merged.chunked(CHUNK_SIZE).forEach { chunk ->
            withContext(Dispatchers.IO) {
                roomRepo.upsertAll(chunk)
            }
            inserted += chunk.size
            updateDbProgress(inserted, total)
        }
    }

    // UI 狀態處理

    private fun updateProgress() {
        _uiState.update {
            it.copy(
                progress = API_PROGRESS_WEIGHT,
                stage = SplashStage.ApiComplete
            )
        }
    }

    private fun updateDbProgress(inserted: Int, total: Int) {
        val progress = API_PROGRESS_WEIGHT +
                (inserted.toFloat() / total) * DB_PROGRESS_WEIGHT

        _uiState.update {
            it.copy(
                progress = progress,
                stage = SplashStage.DBWriting
            )
        }
    }

    private fun handleError(e: Throwable) {
        loge("Splash getData error", e)
        _uiState.update {
            it.copy(
                stage = SplashStage.Error(e.message ?: "Unknown error")
            )
        }
    }
}

fun <T> ResultState<T>.getOrThrow(): T =
    when (this) {
        is ResultState.Success -> data
        is ResultState.Error -> throw Exception(message)
    }

data class SplashUiState(
    val progress: Float = 0f,
    val stage: SplashStage = SplashStage.Idle,
    var timerFinished: Boolean = false,
    var dataFinished: Boolean = false
) {
    val canNavigate: Boolean
        get() = timerFinished && dataFinished
}

sealed class SplashStage {
    object Idle : SplashStage()
    object DBWriting : SplashStage()
    object ApiComplete : SplashStage()
    data class Error(val msg: String) : SplashStage()
}