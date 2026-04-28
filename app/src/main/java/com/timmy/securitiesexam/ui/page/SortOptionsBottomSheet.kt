package com.timmy.securitiesexam.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.FragmentBottomSheetBinding
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.setRippleBackground
import dagger.hilt.android.AndroidEntryPoint

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
    }

    private fun initView() = binding.run {
        tvSortDesc.setRippleBackground(getResourceColor(R.color.ripple))
        tvSortAsc.setRippleBackground(getResourceColor(R.color.ripple))
    }

    private fun initEvent() = binding.run {
        tvSortDesc.click {
            dataViewModel.switchSequence(false) // 降序
            dismiss()
        }

        tvSortAsc.click {
            dataViewModel.switchSequence(true) // 升序
            dismiss()
        }
    }

    // 背景透明的主題
    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme
}