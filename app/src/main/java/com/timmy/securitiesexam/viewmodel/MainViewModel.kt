package com.timmy.securitiesexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmy.assetslibs.repo.AssetsRepository
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.roomlibs.repo.RoomRepository
import com.timmy.securitiesexam.data.SortItem
import com.timmymike.logtool.toDataBeanList
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
    private val asRepo: AssetsRepository,
) : ViewModel() {

    // 當前畫面顯示資料
    private val _uiData = MutableStateFlow(listOfNotNull<StockEntity>())
    val uiData = _uiData.asStateFlow()

    // 當前資料排序 // true為升序，false為降序 // 修改建議後將刪除
    private var sequenceAscending: Boolean = false // 預設為降序

    // 1. 定義排序狀態的 Flow
    private val _sortOption = MutableStateFlow(SortOption())
    val sortOption = _sortOption.asStateFlow()

    private var currentOffset = 0
    private var isLastPage = false
    private var isLoading = false

    /**
     * 供 UI 呼叫的切換方法
     */
    fun updateSort(column: String, isAscending: Boolean) {
        _sortOption.value = SortOption(column = column, isAscending = isAscending)
        resetPagination()
        fetchStockData()
    }

    // 修改建議後將刪除
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
            val newData = roomRepo.getSortedStocks(sortOption.value.column, sortOption.value.isAscending, currentOffset)

            if (newData.isEmpty()) {
                isLastPage = true
            } else {
                // 將新資料累加到舊資料後面
                val updatedList = _uiData.value.toMutableList().apply {
                    addAll(newData)
                }
                _uiData.value = updatedList

                // 增加 offset，供下次使用
                currentOffset += RoomRepository.LIMIT
            }
            isLoading = false
        }
    }

    // 當切換排序時，重置所有狀態
    fun resetPagination() {
        currentOffset = 0
        isLastPage = false
        _uiData.value = emptyList()
    }

    //  getAssetsContent 會切換到 IO 執行緒由方法內部處理
    suspend fun getSortItems(): List<SortItem> {
        return asRepo.getAssetsContent("sortOption.json").toDataBeanList<SortItem>()?.map { item ->
            item.copy(isSelected = item.sortOption == sortOption.value.column)
        } ?: emptyList()
    }

}

// 資料排序選項
data class SortOption(
    val column: String = "code",
    val isAscending: Boolean = false  // 預設為降序
)