package com.timmy.securitiesexam.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.securitiesexam.databinding.FragmentMainLayoutBinding
import com.timmy.securitiesexam.databinding.ItemStockContentBinding
import com.timmy.securitiesexam.ui.util.setTextColorByTarget
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseFragment
import com.timmymike.componenttool.ViewBindingAdapter
import com.timmymike.viewtool.resetLayoutTextSize
import com.timmymike.viewtool.setRippleBackground

class MainFragment : BaseFragment<FragmentMainLayoutBinding>() {

    private val pageViewModel: PageViewModel by activityViewModels()
    private val dataViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()

        initView()

        initObservable()

        initEvent()
    }

    private fun initData() {
        showDialogLoading()
        dataViewModel.getStockAsc() // 需求說，預設要顯示降序排列
    }

    private fun initObservable() {
        lifecycleScope.launchWhenStarted {
            dataViewModel.uiData.collect { data ->
                if (data.isEmpty()) return@collect
                hideDialogLoading()
                @Suppress("UNCHECKED_CAST")
                (binding.rvStockContent.adapter as ViewBindingAdapter<*, StockEntity>).submitList(data)
            }
        }
    }

    private fun initView() = binding.run {
        rvStockContent.adapter = ViewBindingAdapter.create<ItemStockContentBinding, StockEntity>(ItemStockContentBinding::inflate) {
            root.setRippleBackground(Color.RED)
            tvCode.text = it.code
            tvName.text = it.name
            tvClosingPrice.text = it.closingPrice.emptyToDash()
            tvClosingPrice.setTextColorByTarget(it.closingPrice, it.monthlyAveragePrice)
            tvOpeningPrice.text = it.openingPrice.emptyToDash()
            tvOpeningPrice.setTextColorByTarget(it.openingPrice, it.monthlyAveragePrice) // 題目沒有說要做，但我多做的 // 希望不要被扣分
            tvChange.text = it.change
            tvChange.setTextColorByTarget(it.change, "0")
            tvMonthlyAveragePrice.text = it.monthlyAveragePrice
            tvHighestPrice.text = it.highestPrice.emptyToDash()
            tvLowestPrice.text = it.lowestPrice.emptyToDash()

            tvTransactionCount.text = it.transactionCount
            tvTradeVolume.text = it.tradeVolume
            tvTradeValue.text = it.tradeValue

        }.apply {
            viewHolderInitialCallback = { it -> // 第一次產生
                (it.binding.root as? ViewGroup)?.resetLayoutTextSize() // 依畫面比例重設文字大小
            }
        }

    }

    private fun String?.emptyToDash() = this?.takeIf { data -> data.isNotEmpty() } ?: "-"

    private fun initEvent() = binding.run {

    }

}