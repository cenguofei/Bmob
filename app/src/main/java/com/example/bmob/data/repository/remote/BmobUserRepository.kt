package com.example.bmob.data.repository.remote

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobSMS
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
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
    fun loginByUsername(userName:String,pwd:String,callback: (Boolean,String)->Unit){
        BmobUser().run {
            username = userName
            setPassword(pwd)
            login(object :SaveListener<User>(){
                override fun done(p0: User?, p1: BmobException?) {
                    if(p1 == null){
                        callback.invoke(true, EMPTY_TEXT)
                    }else{
                        callback.invoke(false,p1.message ?: EMPTY_TEXT)
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
    fun signup(userName:String,workNum:String,phoneNumber: String,pwd: String, identify:Int, callback: (Boolean)->Unit){
        User(identification = identify).run {
            username = workNum
            name = userName
            mobilePhoneNumber = phoneNumber
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

    /**
     * 注册步骤
     * 获取验证码
     * 请求登录或注册操作的短信验证码
     */
    fun getSignupCode(phoneNumber: String,callback: (isResponseSuccess:Boolean,msgCode:String,msg:String) -> Unit){
        BmobSMS.requestSMSCode(phoneNumber,"",object :QueryListener<Int>(){
            override fun done(p0: Int?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(true,p0.toString(), EMPTY_TEXT)
                }else{
                    callback.invoke(false,FAILED_REQUEST_SMS_CODE.toString(),p1.message.toString())
                }
            }
        })
    }
    /**
     * 一键注册或登录的同时保存其他字段的数据
     * @param phoneNumber
     * @param msgCode
     */
    fun signOrLogin(userName:String,workNum:String,pwd: String, identify:Int,phoneNumber: String,msgCode:String,callback: (isSuccess:Boolean,msg:String)->Unit){
        with(User(identification = identify)){
            name = userName
            username = workNum
            setPassword(pwd)
            mobilePhoneNumber = phoneNumber
            signOrLogin(msgCode,object :SaveListener<User>(){
                override fun done(p0: User?, p1: BmobException?) {
                    if(p1 == null){
                        callback.invoke(true, EMPTY_TEXT)
                    }else{
                        Log.v(LOG_TAG,"验证失败：${p1.message.toString()}")
                        callback.invoke(false,p1.message.toString())
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
    //手机号码重置密码
    //1. 请求重置密码操作的短信验证码
    fun findPassword(phoneNumber:String,callback: (smsId:Int,error:String?) -> Unit){
        BmobSMS.requestSMSCode(phoneNumber,"",object :QueryListener<Int>(){
            override fun done(p0: Int?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(p0!!, EMPTY_TEXT)
                }else{
                    callback.invoke(FAILED_REQUEST_SMS_CODE,p1.message.toString())
                }
            }
        })
    }
    //2. 然后执行验证码的密码重置操作
    fun verifyCode(smsId:String,newPassword:String,callback: (isResetSuccess:Boolean,msg:String) -> Unit){
        BmobUser.resetPasswordBySMSCode(smsId,newPassword,object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0 == null){
                    callback.invoke(true, EMPTY_TEXT)
                }else{
                    callback.invoke(false,p0.message.toString())
                }
            }
        })
    }
}

private const val EMPTY_TEXT = ""
const val FAILED_REQUEST_SMS_CODE = -1












