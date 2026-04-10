package com.timmy.securitiesexam.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.FragmentMainLayoutBinding
import com.timmy.securitiesexam.ui.util.setSelectStyle
import com.timmy.securitiesexam.ui.util.setSelectStyleNotBackGround
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseFragment
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getScreenHeightPixels

class MainFragment : BaseFragment<FragmentMainLayoutBinding>() {

    private val pageViewModel: PageViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        initEvent()
    }

    private fun initView() = binding.run {
    }

    private fun initEvent() = binding.run {

    }

}