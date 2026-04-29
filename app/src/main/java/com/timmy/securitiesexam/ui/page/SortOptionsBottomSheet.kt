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
import com.timmy.securitiesexam.databinding.ItemSortOptionVertBinding
import com.timmy.securitiesexam.ui.util.getSelectBack
import com.timmy.securitiesexam.ui.util.getUnSelectBack
import com.timmy.securitiesexam.ui.util.setRipple
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmymike.componenttool.ViewBindingAdapter
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getScreenHeightPixels
import com.timmymike.viewtool.resetLayoutTextSize
import com.timmymike.viewtool.resetTextSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author timmy
 *
 * [SortOptionsBottomSheet] 針對窄螢幕 (直向模式) 優化的排序選擇器。
 *
 * 主要功能：
 * 1. 互動體驗：實作符合 Material Design 3 規範的底部滑出式選單，便於單手操作。
 * 2. 狀態連動：當使用者選擇排序方式後，將參數回傳至 MainViewModel 以觸發數據重整。
 * 3. UX 優化：點擊選項後自動收起選單，並提供明確的視覺回饋。
 */
@AndroidEntryPoint
class SortOptionsBottomSheet() : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null

    private val binding get() = _binding!!

    private val dataViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        initEvent()

        initObservable()
    }


    private fun initView() = binding.run {
        root.resetLayoutTextSize()
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
                    tvSortAsc.background = requireActivity().getSelectBack()
                } else {
                    tvSortDesc.background = requireActivity().getSelectBack()
                }
                tvSortDesc.setRipple()
                tvSortAsc.setRipple()
            }
        }
    }

    private fun updateUI(list: List<SortItem>) = binding.run {
        rvSortOption.adapter = ViewBindingAdapter.create<ItemSortOptionVertBinding, SortItem>(ItemSortOptionVertBinding::inflate) { data, p ->
            tvSortItem.text = data.sortName
            tvSortItem.background = if (data.isSelected) {
                requireActivity().getSelectBack()
            } else
                requireActivity().getUnSelectBack()

            tvSortItem.setRipple()

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
            submitList(list)
        }
        rvSortOption.scrollToPosition(list.indexOfFirst { it.isSelected == true } - 2)
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

            // 3. 禁用「可摺疊」功能，讓它只能「開啟」或「關閉」
            behavior.skipCollapsed = true
        }
    }

    // 背景透明的主題
    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}