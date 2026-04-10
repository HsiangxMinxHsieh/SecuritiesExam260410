package com.timmy.securitiesexam.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.ActivitySplashBinding
import com.timmy.securitiesexam.viewmodel.DataViewModel
import com.timmy.securitiesexam.viewmodel.SplashStage
import com.timmy.securitiesexam.viewmodel.SplashUiState
import com.timmymike.componenttool.BaseActivity
import com.timmymike.logtool.forJsonAndLoge
import com.timmymike.logtool.loge
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val viewModel: DataViewModel by viewModels()

    private var splashJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loge("splash 初始化")

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
        viewModel.getData()
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
                    state.forJsonAndLoge("當前進度=>")

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
        binding.pgDownload.progress =
            (state.progress.coerceIn(0f, 1f) * 1000).toInt()
    }

    /**
     * Handle business stage UI hint
     */
    private fun handleStage(stage: SplashStage) {
        when (stage) {
            SplashStage.DBWriting -> {
                binding.tvDownloadSubTitle.text =
                    getString(R.string.splash_writing)
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

        loge("splash 即將跳頁")

        gotoActivity(MainActivity::class.java, closeSelf = true)
    }

    companion object {
        private const val SPLASH_MIN_DURATION = 3_000L
    }
}