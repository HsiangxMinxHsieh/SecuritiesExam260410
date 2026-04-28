package com.timmy.securitiesexam.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.ActivityMainBinding
import com.timmy.securitiesexam.ui.page.MainFragment
import com.timmy.securitiesexam.ui.page.SortOptionSideSheet
import com.timmy.securitiesexam.ui.page.SortOptionsBottomSheet
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseToolBarActivity
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.getRoundBgById
import com.timmymike.viewtool.setMarginByDpUnit
import com.timmymike.viewtool.setRippleBackground
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author timmy
 *
 * [MainActivity] 應用程式主進入點容器。
 *
 * 主要功能：
 * 1. 作為導航容器 (Fragment Container)，管理不同業務頁面的切換。
 * 2. 實作沉浸式狀態列適配，優化視覺體驗。
 * 3. 處理螢幕配置變更 (Configuration Changes)，動態調整 UI 佈局 (如 SideSheet / BottomSheet)。
 */
@AndroidEntryPoint
class MainActivity : BaseToolBarActivity<ActivityMainBinding>() {

    private val pageViewModel: PageViewModel by viewModels()
    private val dataViewModel: MainViewModel by viewModels()

    // 持有引用以便管理 // 要可為null // 若用lateinit的話，會在onPause時一樣要判斷是否有指定過值
    private var activeSortMenu: Dialog? = null
    private var activeBottomSheet: SortOptionsBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initEvent()

        initView()

        initObservable()

    }

    private fun initView() = binding.run {
        setToolbarVisible(false)
        pageViewModel.viewModelSwitchFragment(MainFragment())
        ivMenu.apply {
            setMarginByDpUnit(0, 33, 8, 0) // 時間不夠詳細研究，寫死
            background = (getRoundBgById(5, R.color.theme_light, R.color.icon_stroke, 1))
            setRippleBackground(getResourceColor(R.color.icon_stroke))
        }
    }


    private fun initObservable() {
        pageViewModel.switchFragmentLiveData.observe(this@MainActivity) {
            switchFragment(it)
        }

    }

    private fun initEvent() = binding.run {
        ivMenu.click {
            showSortMenu()
        }
    }

    // 這裡要判斷當前螢幕是水平還是垂直，去顯示不同的側欄
    private fun showSortMenu() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // 顯示 SideSheet
            activeSortMenu = SortOptionSideSheet(this@MainActivity, dataViewModel).apply {
                show()
            }
        } else {
            // 顯示 BottomSheet
            activeBottomSheet = SortOptionsBottomSheet().also {
                it.show(supportFragmentManager, it.javaClass.name)
            }
        }
    }

    /** 處理螢幕轉向時關閉選單 */
    override fun onPause() {
        super.onPause()
        // 當 Activity 即將因旋轉重啟時，主動關閉 Dialog 與 Fragment
        activeSortMenu?.dismiss()
        activeBottomSheet?.dismiss()

        activeSortMenu = null
        activeBottomSheet = null
    }

    @SuppressLint("CommitTransaction")
    fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.findFragmentById(binding.containerContent.id)?.let {
                if (judgeIsNeedHideFragment(it)) transaction.hide(it) else transaction.remove(it)
            }

            supportFragmentManager.findFragmentByTag(fragment.javaClass.name)?.let {
                // 顯示已存在的 Fragment
                transaction.show(it)
            } ?: run {
                // 新增 Fragment
                transaction.add(binding.containerContent.id, fragment, fragment.javaClass.name)
            }
            transaction.commitAllowingStateLoss()

    }

    /** 是否僅隱藏（hide）而不移除，以保留在 FragmentManager 堆疊中。 */
    private fun judgeIsNeedHideFragment(fragment: Fragment): Boolean = listOf(
        MainFragment::class
    ).any { fragment::class == it }

}