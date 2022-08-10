package com.example.bmoblibrary.base.basefragment

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.example.bmoblibrary.ext.hideSoftKeyboard

abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : BaseVmVbFragment<VM, VB>() {

    override fun initView(savedInstanceState: Bundle?){}

    /**
     * 创建LiveData观察者 Fragment执行onViewCreated后触发
     */
    override fun createObserver() {}

    override fun setEventListener() {}
}