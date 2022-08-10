package com.example.bmoblibrary.base.baseactivity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.example.bmoblibrary.utils.inflateVmVbBinding

abstract class BaseVmVbActivity<VM : ViewModel, VB : ViewBinding> : BaseVmActivity<VM>() {

    lateinit var binding: VB

    /** 创建DataBinding */
    override fun initViewBinding(): View? {
        binding = inflateVmVbBinding(layoutInflater)
        return binding.root
    }
}