package com.example.bmob.myapp

import com.example.bmob.data.entity.User
import com.example.bmob.viewmodels.AppViewModel

/** 全局viewModel，保存基本的配置信息 */
val appViewModel by lazy { APP.appViewModelInstance }

val appUser: User
    get() = appViewModel.user.value!!


class APP : BaseApp() {
    companion object {
        lateinit var appViewModelInstance: AppViewModel
    }

    override fun onCreate() {
        super.onCreate()
        appViewModelInstance = getAppViewModelProvider().get(AppViewModel::class.java)
    }
}