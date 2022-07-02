package com.example.bmob.data.repository.remote

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import com.example.bmob.data.entity.User
import com.example.bmob.utils.LOG_TAG


class BmobUserRepository private constructor(){

    companion object{
        @Volatile private var INSTANCE:BmobUserRepository? = null
        //单例模式，获取实例
        fun getInstance() = INSTANCE ?: synchronized(this){
            if (INSTANCE == null){
                INSTANCE = BmobUserRepository()
            }
            INSTANCE!!
        }
    }

    /**
     * 用过username登录
     */
    fun loginByUsername(userName:String,pwd:String,callback: (Boolean)->Unit){
        BmobUser().run {
            username = userName
            setPassword(pwd)
            login(object :SaveListener<User>(){
                override fun done(p0: User?, p1: BmobException?) {
                    if(p1 == null){
                        callback.invoke(true)
                    }else{
                        callback.invoke(false)
                        Log.v(LOG_TAG,"登录失败：${p1.message}")
                    }
                }
            })
        }
    }
    //退出登录
    fun logout(){
        BmobUser.logOut()
    }
    //账号密码注册
    fun register(userName:String,pwd:String,callback: (Boolean)->Unit){
        User().run {
            username = userName
            setPassword(pwd)
            signUp(object :SaveListener<User>(){
                override fun done(p0: User?, p1: BmobException?) {
                    if (p1 == null){
                        callback.invoke(true)
                    }else{
                        callback.invoke(false)
                        Log.v(LOG_TAG,"${p1.message}")
                    }
                }
            })
        }
    }
    //获取用户信息
    fun getUserInfo(callback:(Boolean,User?)->Unit){
        BmobQuery<User>().getObject(BmobUser.getCurrentUser().objectId,object :QueryListener<User>(){
            override fun done(p0: User?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(true,p0!!)
                }else{
                    callback.invoke(false,null)
                }
            }
        })
    }
}


