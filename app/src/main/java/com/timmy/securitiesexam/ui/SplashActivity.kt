package com.timmy.securitiesexam.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.timmy.base.cons.ResultConst
import com.timmy.securitiesexam.databinding.ActivitySplashBinding
import com.timmy.securitiesexam.viewmodel.DataViewModel
import com.timmymike.componenttool.BaseActivity
import com.timmymike.componenttool.BaseToolBarActivity
import com.timmymike.logtool.loge
import com.timmymike.timetool.TimeUnits
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val splashDelay = 3 * TimeUnits.oneSec // 固定至少等待三秒

    private val totalWaitCount = 2 // 預期完成的條件數（含等待 splashDelay 計時）
    // 1. 等待 splashDelay 秒

    private var isCompleted = 0

    private val dataViewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initSDK()

        initView()

        initData()

        initObservable()
    }

    private fun initSDK() {
    }

    private fun initObservable() = dataViewModel.run {
        binding.pgDownload.isVisible = true
        lifecycleScope.launch {
            progress.collect { progress ->
                binding.pgDownload.progress = (progress.progress * 1000).toInt()
                loge("當前進度=>${progress},,,狀態判斷結果是=>${progress.stage == ResultConst.Complete}")
                if (progress.stage == ResultConst.Complete) {
                    stepSuccess()
                }
            }
        }
    }

    private fun initView() = binding.run {
        supportActionBar?.hide() // 不使用原生的Title。
    }

    private fun initData() {
        dataViewModel.getData()

        // 啟動延遲計時(避免等待太久)
        lifecycleScope.launch {
            delay(splashDelay)
            stepSuccess()
        }
    }

    private fun stepSuccess() {
        isCompleted++
        if (isCompleted >= totalWaitCount) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        gotoActivity(MainActivity::class.java, closeSelf = true)
    }

}