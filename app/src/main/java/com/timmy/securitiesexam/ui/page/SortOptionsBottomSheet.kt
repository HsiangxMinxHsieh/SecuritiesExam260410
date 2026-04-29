package com.timmy.securitiesexam.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.data.SortItem
import com.timmy.securitiesexam.databinding.FragmentBottomSheetBinding
import com.timmy.securitiesexam.databinding.ItemSortOptionBinding
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmymike.componenttool.ViewBindingAdapter
import com.timmymike.logtool.loge
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.getScreenHeightPixels
import com.timmymike.viewtool.setRippleBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author timmy
 *
 * [SortOptionBottomSheet] 針對窄螢幕 (直向模式) 優化的排序選擇器。
 *
 * 主要功能：
 * 1. 互動體驗：實作符合 Material Design 3 規範的底部滑出式選單，便於單手操作。
 * 2. 狀態連動：當使用者選擇排序方式後，將參數回傳至 MainViewModel 以觸發數據重整。
 * 3. UX 優化：點擊選項後自動收起選單，並提供明確的視覺回饋。
 */
@AndroidEntryPoint
class SortOptionsBottomSheet() : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentBottomSheetBinding
    private val binding get() = _binding

    private val dataViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        initEvent()

        loadDataToShow()
    }


    private fun initView() = binding.run {
        tvSortDesc.setRippleBackground(getResourceColor(R.color.ripple))
        tvSortAsc.setRippleBackground(getResourceColor(R.color.ripple))
    }

    private fun initEvent() = binding.run {
        tvSortDesc.click {
            dataViewModel.updateSort(dataViewModel.sortOption.value.column, false) // 降序
            dismiss()
        }

        tvSortAsc.click {
            dataViewModel.updateSort(dataViewModel.sortOption.value.column, true) // 升序
            dismiss()
        }
    }

    private fun loadDataToShow() = binding.run {
        lifecycleScope.launch {
            // 在 Main執行序更新內容
            updateUI(dataViewModel.getSortItems())
        }
    }

    private fun updateUI(value: List<SortItem>) = binding.run {
        rvSortOption.adapter = ViewBindingAdapter.create<ItemSortOptionBinding, SortItem>(ItemSortOptionBinding::inflate) { data, p ->
            tvSortTitle.text = data.sortName
            root.click {
                data.sortOption?.let {
                    dataViewModel.updateSort(it, dataViewModel.sortOption.value.isAscending)
                    loge("即將執行=>${it}的篩選")
                } ?: return@click
                dismiss()
            }
        }.apply {
            submitList(value)
        }
    }

    // 主要是要在onStart內，調整頁面高度
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        // 取得 BottomSheet 的內部 Container (這是系統自動生成的父佈局)
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let { sheet ->

            val behavior = BottomSheetBehavior.from(sheet)

            // 1. 強制設定 Behavior 的高度與佈局一致
            sheet.layoutParams = sheet.layoutParams.apply {
                height = getScreenHeightPixels() / 4
            }

            // 2. 強制展開狀態
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // 3. (選配) 禁用「可摺疊」功能，讓它只能「開啟」或「關閉」
            behavior.skipCollapsed = true
        }
    }

    // 背景透明的主題
    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme
}