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

    private val _uiData = MutableStateFlow(listOfNotNull<StockEntity>())
    val uiData = _uiData.asStateFlow()

    fun getStockDesc() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiData.emit(roomRepo.getDataDesc())
        }
    }

    fun getStockAsc() {
        viewModelScope.launch {
            _uiData.emit(roomRepo.getDataAsc())
        }
    }

}