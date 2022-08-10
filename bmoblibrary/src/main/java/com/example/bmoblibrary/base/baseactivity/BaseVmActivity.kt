package com.example.bmoblibrary.base.baseactivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bmoblibrary.base.IBase
import com.example.bmoblibrary.utils.getVmClass

abstract class BaseVmActivity<VM : ViewModel> : AppCompatActivity(),IBase {
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initViewBinding())
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(getVmClass(this))
        initView(savedInstanceState)
        createObserver()
        setEventListener()
    }

    open fun initViewBinding(): View? {
        return null
    }
}