package com.timmy.securitiesexam.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.timmy.securitiesexam.databinding.FragmentMainLayoutBinding
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseFragment

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