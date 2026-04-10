package com.timmy.securitiesexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmy.assetslibs.repo.GetAPIRepository
import com.timmy.base.baseResponse.ResultState
import com.timmy.base.cons.ResultConst
import com.timmy.roomlibs.repo.RoomRepository
import com.timmymike.logtool.loge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _progress = MutableStateFlow(ProgressState(0f, ResultConst.Idle))
    val progress = _progress.asStateFlow()

    fun getData() {
        viewModelScope.launch {
            try {

                //  第一階段：API讀取
                val bbuDeferred = async { apiRepo.getBBUData() }
                val avgDeferred = async { apiRepo.getStockAVG() }
                val stockDeferred = async { apiRepo.getStock() }

                var apiProgress = 0f
                val apiStep = API_STATE_PROGRESS / 3f

                fun updateApiProgress(type: String) {
                    apiProgress += apiStep
                    _progress.value = ProgressState(apiProgress / 100f, ResultConst.Success(type))
                }

                val bbu = processApiResult(bbuDeferred.await())
                updateApiProgress("BBU")
                val avg = processApiResult(avgDeferred.await())
                updateApiProgress("AVG")

                val stock = processApiResult(stockDeferred.await())
                updateApiProgress("Stock")

                //  第二階段：DB 寫入
                val totalCount =
                    bbu.size + avg.size + stock.size

                var insertedCount = 0

                suspend fun updateInsertProgress(count: Int, type: String) {
                    insertedCount += count
                    val dbProgress = (insertedCount.toFloat() / totalCount) * DB_STATE_PROGRESS
                    val totalProgress = API_STATE_PROGRESS + dbProgress
                    _progress.value = ProgressState(totalProgress / 100f, ResultConst.Success(type))
                }
                // BBU
                insertChunked(
                    data = bbu,
                    chunkSize = CHUNK_SIZE,
                    insert = { roomRepo.insertByBBU(it) },
                    onChunkInserted = { size ->
                        updateInsertProgress(size, "BBU DB")
                    }
                )

                // AVG
                insertChunked(
                    data = avg,
                    chunkSize = CHUNK_SIZE,
                    insert = { roomRepo.insertByStockAVG(it) },
                    onChunkInserted = { size ->
                        updateInsertProgress(size, "AVG DB")
                    }
                )

                // STOCK
                insertChunked(
                    data = stock,
                    chunkSize = CHUNK_SIZE,
                    insert = { roomRepo.insertByStock(it) },
                    onChunkInserted = { size ->
                        updateInsertProgress(size, "Stock DB")
                    }
                )

                _progress.value = ProgressState(1f, ResultConst.Complete)

            } catch (e: Exception) {
                loge("錯誤", e)
            }
        }
    }

    //  Result 處理（回傳 data）
    private fun <T> processApiResult(result: ResultState<T>): T {
        return when (result) {
            is ResultState.Success -> result.data
            is ResultState.Error -> throw Exception(result.message)
        }
    }

    //  Chunk insert（進度計算核心）
    private suspend fun <T> insertChunked(
        data: List<T>,
        chunkSize: Int,
        insert: suspend (List<T>) -> Unit,
        onChunkInserted: suspend (Int) -> Unit
    ) {
        val chunks = data.chunked(chunkSize)

        for (chunk in chunks) {
            withContext(Dispatchers.IO) {
                insert(chunk)
            }
            onChunkInserted(chunk.size)
        }
    }
}

data class ProgressState(
    val progress: Float,
    val stage: ResultConst
)

sealed class UiState {
    object Idle : UiState()
    data class Loading(val progress: Int) : UiState() // 0~100
    object Success : UiState()
    data class Error(val message: String) : UiState()
}