package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.School
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobUserRepository
import com.example.bmob.utils.LOG_TAG

class BmobUserViewModel:ViewModel() {
    companion object{
        private val userRepository = BmobUserRepository.getInstance()
    }
    fun loginByUsername(userName:String,pwd:String,callback: (Boolean,String)->Unit){
        userRepository.loginByUsername(userName,pwd,callback)
    }
    fun getUserInfo(callback:(isSuccess:Boolean,user:User?)->Unit){
        userRepository.getUserInfo(callback)
    }

    fun getUserInfoByUsername(username:String,callback:(isSuccess:Boolean,user:User?,error:String)->Unit){
        BmobQuery<User>()
            .addWhereEqualTo("username",username)
            .findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if (p1 == null && p0 != null && p0.isNotEmpty()){
                    callback.invoke(true,p0[0], EMPTY_CONTENT)
                    Log.v(LOG_TAG,"找到用户：${p0[0]}")
                }else{
                    callback.invoke(false,null, p1?.message.toString())
                }
            }
        })
    }

    /**
     * 获取验证码
     * 请求登录或注册操作的短信验证码
     */
    fun getSignupCode(phoneNumber: String,callback: (isResponseSuccess:Boolean,msgCode:String,msg:String) -> Unit){
        userRepository.getSignupCode(phoneNumber,callback)
    }
    /**
     * 一键注册或登录的同时保存其他字段的数据
     * @param phoneNumber
     * @param msgCode
     */
    fun signOrLogin(userName:String,workNum:String,pwd: String, identify:Int,phoneNumber: String,msgCode:String,
                    s:String,
                    d:String,
                    c:String,
                    callback: (isSuccess:Boolean,msg:String)->Unit){
        userRepository.signOrLogin(userName,workNum,pwd,identify,phoneNumber,msgCode,
            s,d,c,
            callback)
    }

    //退出登录
    fun logout(){
        BmobUser.logOut()
    }
    fun isLogin():Boolean{
        return BmobUser.isLogin()
    }

    //手机号码重置密码
    //1. 请求重置密码操作的短信验证码
    fun findPassword(phoneNumber:String,callback: (isSuccess: Boolean,smsId:Int,error:String?) -> Unit){
        userRepository.findPassword(phoneNumber,callback)
    }
    //2. 然后执行验证码的密码重置操作
    fun verifyCode(smsId:String,newPassword:String,callback: (isResetSuccess:Boolean,msg:String) -> Unit){
        userRepository.verifyCode(smsId,newPassword,callback)
    }

    /**
     * 模糊查询
     * 查询学校，系
     */
    fun querySchool(schoolName:String, callback: (isSuccess:Boolean,  school:School?, error:String) -> Unit){
        userRepository.querySchool(schoolName,callback)
    }
}

private const val EMPTY_CONTENT = ""