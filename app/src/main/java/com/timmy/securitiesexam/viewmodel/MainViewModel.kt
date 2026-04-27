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

    // 當前資料位移量
    private val _dataOffset = MutableStateFlow(0)
    val dataOffset = _dataOffset.asStateFlow()

    private var currentOffset = 0
    private var isLastPage = false
    private var isLoading = false
    private val limit = 100

    fun fetchStockData(isReverse: Boolean = true) {
        if (isLoading || isLastPage) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true

            // 根據排序需求呼叫 Dao
            val newData = if (isReverse) {
                roomRepo.getDataDesc(currentOffset)
            } else {
                roomRepo.getDataAsc(currentOffset)
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

    fun getStockDesc() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiData.emit(roomRepo.getDataDesc(dataOffset.value))
        }
    }

    fun getStockAsc() {
        viewModelScope.launch {
            _uiData.emit(roomRepo.getDataAsc(dataOffset.value))
        }
    }

}