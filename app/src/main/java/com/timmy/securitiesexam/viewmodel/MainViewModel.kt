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
 * @author timmy
 *
 * [MainViewModel] 主頁面業務邏輯與數據中心。
 *
 * 主要功能：
 * 1. 股票數據抓取：串接 Repository 獲取最新 TWSE OpenAPI 數據。
 * 2. 排序引擎：實作多種金融指標的降序/升序算法，並保持當前排序狀態。
 * 3. 狀態保持：在螢幕旋轉後維持現有股票列表與滾動位置。
 * 4. 提供 UI 使用的數據封裝，確保數據處理與 UI 渲染徹底分離。
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val roomRepo: RoomRepository,
) : ViewModel() {

    // 當前畫面顯示資料
    private val _uiData = MutableStateFlow(listOfNotNull<StockEntity>())
    val uiData = _uiData.asStateFlow()

    // 當前資料排序 // true為升序，false為降序
    private var sequenceAscending: Boolean = false // 預設為降序

    private var currentOffset = 0
    private var isLastPage = false
    private var isLoading = false
    private val limit = 100

    fun switchSequence(isAscending: Boolean) {
        sequenceAscending = isAscending
        resetPagination()
        fetchStockData()
    }

    fun fetchStockData() {
        if (isLoading || isLastPage) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            // 根據排序需求呼叫 Dao
            val newData = if (sequenceAscending) {
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