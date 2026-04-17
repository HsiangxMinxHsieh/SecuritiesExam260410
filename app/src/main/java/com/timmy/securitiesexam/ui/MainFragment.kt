package com.timmy.securitiesexam.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.timmy.roomlibs.database.tables.stock.StockEntity
import com.timmy.securitiesexam.databinding.FragmentMainLayoutBinding
import com.timmy.securitiesexam.databinding.ItemStockContentBinding
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseFragment
import com.timmymike.componenttool.ViewBindingAdapter
import com.timmymike.viewtool.resetLayoutTextSize

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
        dataViewModel.getStockDesc() // 需求說，預設要顯示降序排列
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
            tvStockCode.text = it.code
            tvStockName.text = it.name
        }.apply {
            viewHolderInitialCallback = { it -> // 第一次產生
                (it.binding.root as? ViewGroup)?.resetLayoutTextSize()
            }
        }

    }

    private fun initEvent() = binding.run {

    }

}