package com.timmy.securitiesexam.ui.page

import android.app.Activity
import com.google.android.material.sidesheet.SideSheetDialog
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.FragmentSideSheetBinding
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.setRippleBackground

/**
 * @author timmy
 *
 * [SortOptionSideSheet] 針對寬螢幕 (橫向模式) 優化的側邊排序選單。
 *
 * 主要功能：
 * 1. 空間利用：利用橫向螢幕寬度餘裕，以 SideSheet 形式呈現，避免遮擋核心數據。
 * 2. 響應式佈局：與 BottomSheet 共享同一套排序邏輯，但根據螢幕旋轉狀態動態選擇顯示形式。
 * 3. 資料控制：實作與 ViewModel 的雙向綁定，確保橫向操作時的排序狀態與直向模式保持同步。
 * 4. 精確佈局：透過動畫流暢地從側邊顯示，提升專業金融工具的視覺質感。
 */
class SortOptionSideSheet(
    private val mActivity: Activity,
    private val dataViewModel: MainViewModel
) : SideSheetDialog(mActivity) {

    private val binding: FragmentSideSheetBinding by lazy {
        FragmentSideSheetBinding.inflate(layoutInflater)
    }

    init {
        setContentView(binding.root)
        initView()
        initEvent()
    }

    private fun initView() = binding.run {
        tvSortDesc.setRippleBackground(mActivity.getResourceColor(R.color.ripple))
        tvSortDesc.text = tvSortDesc.text.toVertical()
        tvSortAsc.setRippleBackground(mActivity.getResourceColor(R.color.ripple))
        tvSortAsc.text = tvSortAsc.text.toVertical()
    }

    private fun initEvent() = binding.run {
        tvSortDesc.click {
            dataViewModel.switchSequence(false)
            dismiss()
        }
        tvSortAsc.click {
            dataViewModel.switchSequence(true)
            dismiss()
        }
    }

    fun CharSequence.toVertical() = this.toString().toCharArray().joinToString("\n")
}