package com.example.bmoblibrary.base.basefragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bmoblibrary.base.IBase
import com.example.bmoblibrary.utils.getVmClass

/**
 * 可以根据需要自定义ViewModel
 */
abstract class BaseVmFragment<VM : ViewModel> : Fragment(),IBase {

    //是否第一次加载
    private var isFirst: Boolean = true

    lateinit var viewModel: VM

    var mActivity: AppCompatActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        viewModel = ViewModelProvider(this).get(getVmClass(this))
        initView(savedInstanceState)
        createObserver()
        setEventListener()
    }
}

