package com.timmy.securitiesexam.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Stack
import javax.inject.Inject

/**
 * @author timmy
 *
 * [PageViewModel] 負責管理應用程式的導航邏輯與全域 UI 狀態。
 *
 * 主要功能：
 * 1. 導航控制：透過封裝的 LiveData/Flow 管理目前應顯示的 Fragment 頁面。
 * 2. 狀態共享：在多個 Fragment（測驗是只有一個） 之間傳遞簡單的導航事件，確保畫面跳轉逻辑不與具體頁面耦合。
 */
@HiltViewModel
class PageViewModel @Inject constructor(
) : ViewModel() {

    // 頁面切換邏輯
    private val _switchFragmentLiveData = MutableLiveData<Fragment>()
    val switchFragmentLiveData: LiveData<Fragment> = _switchFragmentLiveData

    // 上一頁的儲存庫
    private val fragmentStack = Stack<Fragment>()

    fun viewModelSwitchFragment(switchFragment: Fragment) { // 點餐流程中 needReplace 宜為 false，返回主選單時可保留堆疊狀態。
        fragmentStack.push(switchFragment)
        _switchFragmentLiveData.postValue(switchFragment)
    }


}
