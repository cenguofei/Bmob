package com.example.bmoblibrary.base.baseactivity

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.example.bmoblibrary.utils.inflateVbBinding

abstract class BaseVbActivity<VB:ViewBinding>: BmobActivity() {
    /** 该类绑定的ViewDataBinding */
    lateinit var binding:VB

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun createObserver() {
    }

    override fun setEventListener() {
    }

    override fun initViewBinding(): View {
        binding = inflateVbBinding(layoutInflater)
        return binding.root
    }
}