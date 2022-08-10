package com.example.bmoblibrary.base.baseactivity

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : BaseVmVbActivity<VM, VB>() {

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    override fun setEventListener() {}
}