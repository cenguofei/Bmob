package com.example.bmoblibrary.base.baseactivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bmoblibrary.base.IBase

abstract class BmobActivity :AppCompatActivity(),IBase{

    abstract fun initViewBinding(): View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initViewBinding())
        initView(savedInstanceState)
        createObserver()
        setEventListener()
    }
}