package com.example.bmob.data.repository.remote

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobSMS
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.*
import com.example.bmob.data.entity.*
import com.example.bmob.utils.LOG_TAG


class BmobRepository private constructor(){

    companion object{
        @Volatile private var INSTANCE:BmobRepository? = null
        //单例模式，获取实例
        fun getInstance() = INSTANCE ?: synchronized(this){
            if (INSTANCE == null){
                INSTANCE = BmobRepository()
            }
            INSTANCE!!
        }
    }

    fun updateUser(studentSelectState:Boolean,user:User,callback: (isSuccess: Boolean, msg: String) -> Unit){
        user.studentSelectState = studentSelectState
        user.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0 == null){
                    callback.invoke(true, EMPTY_TEXT)
                }else{
                    callback.invoke(false,p0.message.toString())
                }
            }
        })
    }

    /**
     * 通过username登录
     * 也就是工号或学号
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
    fun signOrLogin(userName:String,workNum:String,pwd: String, identify:Int,phoneNumber: String, msgCode:String,
                    s:String,
                    d:String,
                    c:String,
                    callback: (isSuccess:Boolean,msg:String)->Unit){
        with(User(identification = identify)){
            name = userName
            username = workNum
            setPassword(pwd)
            mobilePhoneNumber = phoneNumber

            school = s
            department = d
            college = c

            /**
             * 针对所有用户都统一设置
             * 因为非学生用户根本用不到该属性
             */
            studentSelectState = STUDENT_NOT_SELECT_THESIS

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
    fun getUserInfo(callback:(isSuccess:Boolean,user:User?)->Unit){
        BmobQuery<User>().getObject(BmobUser.getCurrentUser().objectId,object :QueryListener<User>(){
            override fun done(p0: User?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(true,p0!!)
                }
                else{
                    callback.invoke(false,null)
                }
            }
        })
    }
    //手机号码重置密码
    //1. 请求重置密码操作的短信验证码
    fun findPassword(phoneNumber:String,callback: (isSuccess: Boolean,smsId:Int,error:String?) -> Unit){
        BmobSMS.requestSMSCode(phoneNumber,"",object :QueryListener<Int>(){
            override fun done(p0: Int?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(true,p0!!, EMPTY_TEXT)
                }else{
                    callback.invoke(false,FAILED_REQUEST_SMS_CODE,p1.message.toString())
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

    /**
     * 模糊查询
     * 查询学校，系
     */
    fun querySchool(schoolName:String, callback: (isSuccess:Boolean, school:School?, error:String) -> Unit){
        BmobQuery<School>()
//            .addWhereMatches("schoolName","%西南大学%")
//            .addWhereContains("schoolName","西南大学")
            .addWhereEqualTo("schoolName",schoolName)
            .setLimit(1)
//            .addWhereStartsWith("schoolName","西南大学")
//            .addWhereEndsWith("schoolName","西南大学")
            .findObjects(object :FindListener<School>(){
                override fun done(p0: MutableList<School>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.size > 0){
                            callback.invoke(true,p0[0], EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null, "没有匹配信息")
                        }
                    }else{
                        callback.invoke(false,null,p1.message.toString())
                    }
                }
            })

    }

    /**
     * 搜索学生首页的banner
     */
    fun queryBannerData(callback: (isSuccess:Boolean, data:MutableList<BmobBannerObject>?, msg:String) -> Unit){
        BmobQuery<BmobBannerObject>()
            .order("-likes")  // 根据likes字段降序显示数据
            .setLimit(12)
            .findObjects(object :FindListener<BmobBannerObject>(){
            override fun done(p0: MutableList<BmobBannerObject>?, p1: BmobException?) {
                if (p1 == null){
                    if (p0 != null && p0.size > 0){
                        callback.invoke(true,p0, EMPTY_TEXT)
                    }else{
                        callback.invoke(false,null,"没有banner数据")
                    }
                }else{
                    callback.invoke(false,null, p1.message.toString())
                }
            }
        })
    }

    /**
     * 模糊查询能选的文章
     */
    fun searchAnyThesis(searchTitle:String,callback: (isSuccess:Boolean,thesis:Pair<String,MutableList<Thesis>>?,msg:String) -> Unit){
        BmobQuery<Thesis>()
//            .addWhereEqualTo("title",searchTitle)
            .addWhereContains("title",searchTitle)
//            .addWhereMatches("title","")
            .findObjects(object :FindListener<Thesis>(){
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.size > 0){
                            callback.invoke(true,Pair(searchTitle,p0), EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null,"没有匹配项")
                        }
                    }else{
                        callback.invoke(false,null,p1.message.toString())
                    }
                }
            })
    }

    /**
     * 添加Thesis测试方法
     */
    fun addThesis(thesis: Thesis,callback: (isSuccess:Boolean,objectId:String?,msg:String?) -> Unit){
        thesis.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(true,p0!!, EMPTY_TEXT)
                }else{
                    callback.invoke(false,null,p1.message)
                }
            }
        })
    }

    //同步控制台数据到缓存中
    fun fetchUserInfo(){
        BmobUser.fetchUserInfo(object :FetchUserInfoListener<User>(){
            override fun done(p0: User?, p1: BmobException?) {
                if (p1 == null){
                    Log.v(LOG_TAG,"更新用户本地缓存信息成功:${p0?.username}  ${p0?.name}")
                }else{
                    Log.v(LOG_TAG,"更新用户本地缓存信息失败:${p1.message}")
                }
            }
        })
    }

    //下载图片
    fun downloadImage(){
        BmobFile("16570959234301576.jpg","","http://bmob-cdn-30807.bmobpay.com/2022/07/06/2da1c22294734cf2ac8855bf7f271fce.jpg")
            .download(object :DownloadFileListener(){
                override fun done(p0: String?, p1: BmobException?) {
                    if (p1 == null){
                        Log.v(LOG_TAG,"下载成功:p0=$p0")
                    }else{
                        Log.v(LOG_TAG,"下载失败:${p1.message}")
                    }
                }
                override fun onProgress(p0: Int?, p1: Long) {
                    Log.v(LOG_TAG,"p0=$p0  p1=$p1")
                }
            })
    }
}

const val EMPTY_TEXT = ""
const val FAILED_REQUEST_SMS_CODE = -1












