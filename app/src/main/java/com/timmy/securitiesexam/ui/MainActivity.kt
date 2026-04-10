package com.timmy.securitiesexam.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.timmy.securitiesexam.databinding.ActivityMainBinding
import com.timmy.securitiesexam.viewmodel.DataViewModel
import com.timmy.securitiesexam.viewmodel.PageViewModel
import com.timmymike.componenttool.BaseToolBarActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseToolBarActivity<ActivityMainBinding>() {

    private val dataViewModel: DataViewModel by viewModels()
    private val pageViewModel: PageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarVisible(false)

        initEvent()

        initSDK()

        initView()

        initObservable()

        pageViewModel.viewModelSwitchFragment(MainFragment())
    }

    private fun initView() = binding.run {

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
    }


    private fun initSDK() {
//        dataViewModel.getData()
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