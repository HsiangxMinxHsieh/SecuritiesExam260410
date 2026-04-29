package com.timmy.securitiesexam.ui.page

import android.app.Activity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.sidesheet.SideSheetDialog
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.data.SortItem
import com.timmy.securitiesexam.databinding.FragmentSideSheetBinding
import com.timmy.securitiesexam.databinding.ItemSortOptionHoriBinding
import com.timmy.securitiesexam.ui.util.getSelectBack
import com.timmy.securitiesexam.ui.util.getUnSelectBack
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmymike.componenttool.ViewBindingAdapter
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.resetLayoutTextSize
import com.timmymike.viewtool.resetTextSize
import com.timmymike.viewtool.setRippleBackground
import kotlinx.coroutines.launch

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
        initObservable()
    }

    private fun initView() = binding.run {
        root.resetLayoutTextSize()
        tvSortDesc.setRippleBackground(mActivity.getResourceColor(R.color.ripple))
        tvSortDesc.text = tvSortDesc.text.toVertical()
        tvSortAsc.setRippleBackground(mActivity.getResourceColor(R.color.ripple))
        tvSortAsc.text = tvSortAsc.text.toVertical()
    }

    private fun initEvent() = binding.run {
        tvSortDesc.click {
            dataViewModel.updateSort(isAscending = false) // 降序
            dismiss()
        }

        tvSortAsc.click {
            dataViewModel.updateSort(isAscending = true) // 升序
            dismiss()
        }
    }

    private fun initObservable() = binding.run {
        lifecycleScope.launch {
            // 在 Main執行序請求執行更新內容
            updateUI(dataViewModel.getSortItems())

            dataViewModel.sortOption.collect {
                // 由於生命週期的設計，所以以下內容，只會跑一次
                if (it.isAscending == true) {
                    tvSortAsc.background = mActivity.getSelectBack()
                } else {
                    tvSortDesc.background = mActivity.getSelectBack()
                }
                tvSortDesc.setRippleBackground(mActivity.getResourceColor(R.color.ripple))
                tvSortAsc.setRippleBackground(mActivity.getResourceColor(R.color.ripple))
            }
        }
    }

    private fun updateUI(value: List<SortItem>) = binding.run {
        rvSortOption.adapter = ViewBindingAdapter.create<ItemSortOptionHoriBinding, SortItem>(ItemSortOptionHoriBinding::inflate) { data, p ->
            data.sortName?.let { tvSortItem.text = it.toVertical() }
            tvSortItem.background = if (data.isSelected) {
                mActivity.getSelectBack()
            } else
                mActivity.getUnSelectBack()
            tvSortItem.setRippleBackground(mActivity.getResourceColor(R.color.ripple))

            root.click {
                data.sortOption?.let {
                    dataViewModel.updateSort(it)
                } ?: return@click
                dismiss()
            }
        }.apply {
            viewHolderInitialCallback = { it -> // 第一次產生
                it.binding.root.resetTextSize()
            }

            submitList(value)
        }
    }

    fun CharSequence.toVertical() = this.toString().toCharArray().joinToString("\n")


}