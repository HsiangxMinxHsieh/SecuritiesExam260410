package com.timmy.securitiesexam.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.ActivitySplashBinding
import com.timmy.securitiesexam.viewmodel.SplashStage
import com.timmy.securitiesexam.viewmodel.SplashUiState
import com.timmy.securitiesexam.viewmodel.SplashViewModel
import com.timmymike.componenttool.BaseActivity
import com.timmymike.timetool.TimeUnits
import com.timmymike.viewtool.getResourceString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author timmy
 *
 * [SplashActivity] 啟動導引頁面。
 *
 * 主要功能：
 * 1. 初始化應用程式環境與預載核心數據 (透過 SplashViewModel)。
 * 2. 實作啟動計時器 (3秒)，確保品牌曝光並防止網路請求卡死導致黑屏。
 * 3. 監控 API 請求狀態 (Idle, ApiComplete, DBWriting, Error)。
 * 4. 根據業務狀態顯示下載進度 (Progress Bar) 與當前狀態提示文字。
 * 5. 異常處理：當數據獲取失敗時彈出錯誤對話框並支援重新嘗試。
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    companion object {
        private const val SPLASH_MIN_DURATION = TimeUnits.oneSec * 3 // 等待三秒鐘
    }

    private val viewModel: SplashViewModel by viewModels()

    private var splashJob: Job? = null

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initObserver()
        initData()
        startSplashTimer()
    }


    private fun initView() = binding.run {
        supportActionBar?.hide()
        pgDownload.progress = 0
    }

    private fun initData() {
        viewModel.start()
    }

    /**
     * Splash Timer（避免 loading 卡死）
     */
    private fun startSplashTimer() {
        splashJob?.cancel()

        splashJob = lifecycleScope.launch {
            delay(SPLASH_MIN_DURATION)
            viewModel.onSplashTimerFinished()
            handleNavigation()
        }
    }

    /**
     * Observe ViewModel State
     */
    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->
                    if (state.stage == SplashStage.Idle) {
                        return@collect
                    }
                    renderProgress(state)

                    handleStage(state.stage)

                    handleNavigation()
                }
            }
        }
    }

    /**
     * UI render only
     */
    private fun renderProgress(state: SplashUiState) {
        binding.clDownload.isVisible = true
        binding.pgDownload.progress =
            (state.progress.coerceIn(0f, 1f) * 1000).toInt()
    }

    /**
     * Handle business stage UI hint
     */
    private fun handleStage(stage: SplashStage) {
        when (stage) {
            SplashStage.ApiComplete -> {
                binding.tvDownloadSubTitle.text =
                    getString(R.string.splash_downloading)
            }

            SplashStage.DBWriting -> {
                binding.tvDownloadSubTitle.text =
                    getString(R.string.splash_writing)
            }

            is SplashStage.Error -> {
                if (dialog != null) return
                dialog = showMessageDialog(getResourceString(R.string.get_data_error), {
                    viewModel.start()
                    dialog = null
                })
            }

            else -> Unit
        }
    }

    /**
     * Navigation decision centralized
     */
    private fun handleNavigation() {
        if (viewModel.uiState.value.canNavigate) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        splashJob?.cancel()

        gotoActivity(MainActivity::class.java, closeSelf = true)
    }


}