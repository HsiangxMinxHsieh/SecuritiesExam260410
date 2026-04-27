package com.timmy.securitiesexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.roomlibs.repo.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 處理資料的 ViewModel，主要用於登入與資料讀寫。
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val roomRepo: RoomRepository,
) : ViewModel() {

    // 當前畫面顯示資料
    private val _uiData = MutableStateFlow(listOfNotNull<StockEntity>())
    val uiData = _uiData.asStateFlow()

    // 當前資料排序 // true為升序，false為降序
    private var isAscending: Boolean = false // 預設為降序

    private var currentOffset = 0
    private var isLastPage = false
    private var isLoading = false
    private val limit = 100

    fun switchSequence() {
        isAscending = !isAscending
        resetPagination()
        fetchStockData()
    }

    fun switchSequenceToAscending() {
        isAscending = true
        resetPagination()
        fetchStockData()
    }

    fun switchSequenceToDescending() {
        isAscending = false
        resetPagination()
        fetchStockData()
    }

    fun fetchStockData() {
        if (isLoading || isLastPage) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            // 根據排序需求呼叫 Dao
            val newData = if (isAscending) {
                roomRepo.getDataAsc(currentOffset)
            } else {
                roomRepo.getDataDesc(currentOffset)
            }

            if (newData.isEmpty()) {
                isLastPage = true
            } else {
                // 將新資料累加到舊資料後面
                val updatedList = _uiData.value.toMutableList().apply {
                    addAll(newData)
                }
                _uiData.value = updatedList

                // 增加 offset，供下次使用
                currentOffset += limit
            }
            isLoading = false
        }
    }

    // 當切換排序時，記得重置所有狀態
    fun resetPagination() {
        currentOffset = 0
        isLastPage = false
        _uiData.value = emptyList()
    }

}