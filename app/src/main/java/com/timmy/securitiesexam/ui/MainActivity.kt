package com.timmy.securitiesexam.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.timmy.securitiesexam.R
import com.timmy.securitiesexam.databinding.ActivityMainBinding
import com.timmy.securitiesexam.ui.page.MainFragment
import com.timmy.securitiesexam.viewmodel.MainViewModel
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseToolBarActivity
import com.timmymike.logtool.forLoge
import com.timmymike.viewtool.click
import com.timmymike.viewtool.getResourceColor
import com.timmymike.viewtool.getRoundBgById
import com.timmymike.viewtool.setRippleBackground
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseToolBarActivity<ActivityMainBinding>() {

    private val pageViewModel: PageViewModel by viewModels()
    private val dataViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initEvent()

        initView()

        initObservable()

    }

    private fun initView() = binding.run {
        setToolbarVisible(false)
        pageViewModel.viewModelSwitchFragment(MainFragment())
        ivMenu.background = (getRoundBgById(5, R.color.background, R.color.icon_stroke, 1))
        ivMenu.setRippleBackground(getResourceColor(R.color.icon_stroke))
    }


    private fun initObservable() {

        pageViewModel.switchFragmentLiveData.observe(this@MainActivity) {
            switchFragment(it.first, it.second)
        }

        pageViewModel.removeFragmentLiveData.observe(this@MainActivity) {
            removeFragment(it)
        }

        pageViewModel.removeAllFragmentLiveData.observe(this@MainActivity) {
            if (it == 1) {
                clearFragmentStack()
            }
        }

    }

    private fun initEvent() = binding.run {
        ivMenu.click {
            "收到點擊事件".forLoge("更新資料偵錯→")
//            dataViewModel.getStockDesc()
        }
    }

    @SuppressLint("CommitTransaction")
    fun switchFragment(fragment: Fragment, needReplace: Boolean = false) {
//        logWtf("即將switchTo ${fragment},此時的needReplace是=>${needReplace}")
        val transaction = supportFragmentManager.beginTransaction()
        if (needReplace) {
            transaction.replace(binding.containerContent.id, fragment, fragment.javaClass.name).commit()
        } else {
            // 隱藏當前 Fragment
            supportFragmentManager.findFragmentById(binding.containerContent.id)?.let {
                if (judgeIsNeedHideFragment(it)) transaction.hide(it) else transaction.remove(it)
            }

            supportFragmentManager.findFragmentByTag(fragment.javaClass.name)?.let {
                // 顯示已存在的 Fragment
                transaction.show(it)
            } ?: run {
                // 新增 Fragment
                transaction.add(binding.containerContent.id, fragment, fragment.javaClass.name)
            }
            transaction.commitAllowingStateLoss()
        }

    }

    /** 是否僅隱藏（hide）而不移除，以保留在 FragmentManager 堆疊中。 */
    private fun judgeIsNeedHideFragment(fragment: Fragment): Boolean = listOf(
        MainFragment::class
    ).any { fragment::class == it }

    private fun removeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        // 移除傳入的 Fragment
        supportFragmentManager.findFragmentById(binding.containerContent.id)?.let {
            transaction.remove(fragment)
        }

        transaction.commit()

    }

    @SuppressLint("CommitTransaction")
    fun clearFragmentStack() {
        supportFragmentManager.run {
            fragments.forEach {
                beginTransaction().remove(it)
            }
            beginTransaction().commit()
        }
    }

}