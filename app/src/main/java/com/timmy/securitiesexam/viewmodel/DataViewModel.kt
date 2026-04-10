package com.timmy.securitiesexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmy.assetslibs.repo.GetAPIRepository
import com.timmy.base.baseResponse.ResultState
import com.timmy.roomlibs.repo.RoomRepository
import com.timmymike.logtool.loge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
class DataViewModel @Inject constructor(
    private val roomRepo: RoomRepository,
    private val apiRepo: GetAPIRepository
) : ViewModel() {

    companion object {
        const val API_STATE_PROGRESS = 30f
        const val DB_STATE_PROGRESS = 70f
        const val CHUNK_SIZE = 500
    }

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    fun getData() {
        viewModelScope.launch {
            val scope = this

            try {
                // API Phase
                val apiStep = API_STATE_PROGRESS / 3f
                var apiProgress = 0f

                fun updateApiProgress() {
                    apiProgress += apiStep

                    _uiState.update {
                        it.copy(
                            progress = apiProgress / 100f,
                            stage = SplashStage.ApiComplete
                        )
                    }
                }

                val bbuDeferred = scope.async { apiRepo.getBBUData() }
                updateApiProgress()

                val avgDeferred = scope.async { apiRepo.getStockAVG() }
                updateApiProgress()

                val stockDeferred = scope.async { apiRepo.getStock() }
                updateApiProgress()

                val bbu = bbuDeferred.await().getOrThrow()
                val avg = avgDeferred.await().getOrThrow()
                val stock = stockDeferred.await().getOrThrow()

                // DB Phase
                val totalCount = bbu.size + avg.size + stock.size
                var insertedCount = 0

                fun updateInsertProgress(count: Int) {
                    insertedCount += count

                    val progress =
                        API_STATE_PROGRESS +
                                (insertedCount.toFloat() / totalCount) * DB_STATE_PROGRESS
                    _uiState.update { it.copy(progress / 100f, SplashStage.DBWriting) }

                }

                suspend fun <T> insertChunked(
                    data: List<T>,
                    insert: suspend (List<T>) -> Unit
                ) {
                    data.chunked(CHUNK_SIZE).forEach { chunk ->
                        withContext(Dispatchers.IO) {
                            insert(chunk)
                        }
                        updateInsertProgress(chunk.size)
                    }
                }

                insertChunked(bbu, roomRepo::insertByBBU)
                insertChunked(avg, roomRepo::insertByStockAVG)
                insertChunked(stock, roomRepo::insertByStock)

                onDataCompleted()

            } catch (e: Exception) {
                loge("getData錯誤", e)
                _uiState.value = SplashUiState(
                    stage = SplashStage.Error("getData錯誤，錯誤資訊=>${e.message}")
                )
            }
        }
    }

    fun onSplashTimerFinished() {
        loge("splash即將修改 timerFinished 為true")
        _uiState.update { it.copy(timerFinished = true) }
    }

    fun onDataCompleted() {
        loge("splash即將修改 dataFinished 為true")
        _uiState.update { it.copy(dataFinished = true) }
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