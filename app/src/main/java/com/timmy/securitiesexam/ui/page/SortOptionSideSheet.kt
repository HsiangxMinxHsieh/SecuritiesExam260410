package com.timmy.securitiesexam.ui.page

import android.app.Activity
import com.google.android.material.sidesheet.SideSheetDialog
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.FragmentSideSheetBinding
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.setRippleBackground

/** 畫面水平時的側邊排序調整欄
 *
 *
 * */
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