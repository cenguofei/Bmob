package com.example.bmob.fragments.start

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.data.storage.UserConfig
import com.example.bmob.databinding.FragmentStartBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.getSettingsDataStore
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.logI
import java.util.*


class StartFragment : BaseVbFragment<FragmentStartBinding>() {
    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore
    private val userViewModel: BmobUserViewModel by activityViewModels()

    override fun createObserver() {
        settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) {
            Timer().schedule(object : TimerTask() {  //给用户看2秒后跳转到对应页面
                override fun run() {
                    navigateToNext(it)
                }
            }, 2000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsDataStore = getSettingsDataStore()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToNext(userConfig: UserConfig) {
        requireActivity().runOnUiThread {
            if (!userConfig.isRememberPassword) { //如果用户以前没有选择记住密码，不管是新用户还是老用户都需要登录
                findNavController().navigate(R.id.action_startFragment_to_loginFragment)
            } else {
                //判断用户是否处于登录状态
                if (userViewModel.isLogin()) {  //是
                    val bmobUser = BmobUser.getCurrentUser()
                    Log.v(
                        LOG_TAG,
                        "已经登录:账号username：${bmobUser.username} 电话：${bmobUser.mobilePhoneNumber}"
                    )
                    userViewModel.getUserInfo { isSuccess, user ->
                        if (isSuccess) {
                            Log.v(LOG_TAG, "获取用户信息成功")

                            appViewModel.setUser(user!!)

                            userViewModel.getUserIdentificationAndNavigateFromStart(
                                user.identification,
                                this@StartFragment
                            ).also {
                                "+BmobUserViewModel 在StartFragment 设置 ${user.identification}".logI()
                            }
                            "*appViewModel 在StartFragment 设置 ${user.identification}".logI()

                        } else {
                            Log.v(LOG_TAG, "获取用户信息失败")
                            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                        }
                    }
                } else { //不是
                    findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                }
            }
        }
    }
}