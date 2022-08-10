package com.example.bmoblibrary.base

import android.os.Bundle

interface IBase {
    fun setEventListener()

    fun createObserver()

    fun initView(savedInstanceState: Bundle?)
}