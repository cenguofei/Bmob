package com.example.bmob.myapp

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner


/**
 * 在平时的开发中，有时候可能会需要一些全局数据，来让应用中的所有Activity和View都能访问到
 *
 * android:name属性——是用来设置所有activity属于哪个application的，默认是android.app.Application。
 * 这个类(Application)的作用是为了放一些全局的和一些上下文都要用到的变量和方法。
 *
 * 这样就可以将默认的Application给设置成我们自定义的Application
 *
 * 这样处理的好处是：
 *  1. 继承的话，当应用程序退出后其生命周期也跟着结束，
 *  2. 而用静态类的话程序退出后不会立刻被gc回收，当你再次进入后会发现该静态类保存的信息状态是之前的。
 *     有可能会导致程序不是你想要的初始化状态。
 *
 *
 */
open class BaseApp : Application(), ViewModelStoreOwner {

    private lateinit var mViewModelStore: ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }

    override fun onCreate() {
        super.onCreate()
        mViewModelStore = ViewModelStore()

    }

    /** 获取全局的ViewModel */
    fun getAppViewModelProvider(): ViewModelProvider {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return ViewModelProvider(this, mFactory as ViewModelProvider.Factory)
    }
}