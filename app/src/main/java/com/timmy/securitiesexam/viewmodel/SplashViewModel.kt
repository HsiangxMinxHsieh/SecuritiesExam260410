package com.timmy.securitiesexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmy.assetslibs.repo.GetAPIRepository
import com.timmy.base.baseResponse.ResultState
import com.timmy.base.data.response.BBUDataItem
import com.timmy.base.data.response.StockAVGDataItem
import com.timmy.base.data.response.StockDataItem
import com.timmy.datastorelibs.repo.DataStoreRepository
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.roomlibs.repo.RoomRepository
import com.timmy.securitiesexam.data.StockMergeModel
import com.timmymike.logtool.loge
import com.timmymike.timetool.TimeUnits
import com.timmymike.timetool.nowTime
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
 * 處理資料的 ViewModel，主要用於登入與資料讀寫。
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val roomRepo: RoomRepository,
    private val apiRepo: GetAPIRepository,
    private val dsRepo: DataStoreRepository
) : ViewModel() {

    companion object {
        private const val API_PROGRESS_WEIGHT = 0.3f
        private const val DB_PROGRESS_WEIGHT = 0.7f
        private const val CHUNK_SIZE = 500
        private const val GET_DATA_INTERVAL = TimeUnits.oneMin * 10
    }

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    // ===== Public API =====
    private var splashJob: Job? = null
    fun start() {
        if (splashJob?.isActive == true) { // 螢幕轉向時，不重複啟動
            return
        }

        if (!shouldFetchData()) {
            completeData()
            return
        }

        splashJob = viewModelScope.launch {
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

    fun onSplashTimerFinished() {
        _uiState.update { it.copy(timerFinished = true) }
    }

    // ===== Business Logic =====

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
            val item = getOrCreate(it.code.toString())
            item.name = it.name.toString()
            item.dividendYield = it.dividendYield.toString()
            item.pBratio = it.pBratio.toString()
            item.pEratio = it.pEratio.toString()
        }

        // AVG
        avg.forEach {
            val item = getOrCreate(it.code.toString())
            item.name = it.name.toString()
            item.closingPrice = it.closingPrice.toString()
            item.monthlyAveragePrice = it.monthlyAveragePrice.toString()
        }

        // STOCK
        stock.forEach {
            val item = getOrCreate(it.code.toString())
            item.name = it.name.toString()
            item.tradeVolume = it.tradeVolume.toString()
            item.tradeValue = it.tradeValue.toString()
            item.openingPrice = it.openingPrice.toString()
            item.highestPrice = it.highestPrice.toString()
            item.lowestPrice = it.lowestPrice.toString()
            item.closingPrice = it.closingPrice.toString()
            item.change = it.change.toString()
            item.transaction = it.transaction.toString()
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
                pEratio = it.pEratio
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

    // ===== UI State Handling =====

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

    private fun completeData() {
        _uiState.update { it.copy(dataFinished = true) }
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