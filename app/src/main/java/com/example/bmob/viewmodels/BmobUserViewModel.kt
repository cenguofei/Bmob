package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobUserRepository

class BmobUserViewModel:ViewModel() {
    companion object{
        private val userRepository = BmobUserRepository.getInstance()
    }
    fun loginByUsername(userName:String,pwd:String,callback: (Boolean,String)->Unit){
        userRepository.loginByUsername(userName,pwd,callback)
    }
    fun getUserInfo(callback:(Boolean, User?)->Unit){
        userRepository.getUserInfo(callback)
    }
    //手机号码重置密码
    //1. 请求重置密码操作的短信验证码
    fun findPassword(phoneNumber:String,callback: (smsId:Int,error:String?) -> Unit){
        userRepository.findPassword(phoneNumber,callback)
    }
    //2. 然后执行验证码的密码重置操作
    fun verifyCode(smsId:String,newPassword:String,callback: (isResetSuccess:Boolean,msg:String) -> Unit){
        userRepository.verifyCode(smsId,newPassword,callback)
    }
}