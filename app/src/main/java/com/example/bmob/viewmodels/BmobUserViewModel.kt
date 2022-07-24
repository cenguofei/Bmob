package com.example.bmob.viewmodels

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.R
import com.example.bmob.data.entity.*
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.utils.EMPTY_TEXT
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.Username

class BmobUserViewModel(private val handler: SavedStateHandle) : ViewModel() {

    companion object {
        private val repository = BmobRepository.getInstance()
        private const val USER_IDENTIFICATION = "_user_identification_"
    }

    //得到当前用户身份，进入该身份的首页
    fun getUserIdentification(): MutableLiveData<Int> {
        Log.v(LOG_TAG, "MainActivity getUserIdentification")
        if (!handler.contains(USER_IDENTIFICATION)) {
            Log.v(LOG_TAG, "BmobUserViewModel handler没有存储USER_IDENTIFICATION")
            setUserIdentification(USER_HAS_NOT_IDENTIFICATION)
        }
        return handler.getLiveData(USER_IDENTIFICATION)
    }

    //设置当前用户身份
    fun setUserIdentification(identification: Int) {
        Log.v(LOG_TAG, "setUserIdentification:$identification")
        handler.set(USER_IDENTIFICATION, identification)
    }

    fun getUserIdentificationAndNavigateFromVerify(identification: Int, fragment: Fragment) {
        /**
         * setUserIdentification()暂时不起作用
         * 因为MainActivity并没有判断用户身份，然后显示不同的底部导航栏的中间图标
         */
        Log.v(
            LOG_TAG,
            "VerifyFragment getUserIdentificationAndNavigateForVerify 当前身份为：$identification"
        )
        when (identification) {
            IDENTIFICATION_STUDENT -> {
                setUserIdentification(IDENTIFICATION_STUDENT)
                fragment.findNavController()
                    .navigate(R.id.action_verifyFragment_to_studentHomeFragment)
            }
            IDENTIFICATION_TEACHER -> {
                setUserIdentification(IDENTIFICATION_TEACHER)
                fragment.findNavController()
                    .navigate(R.id.action_verifyFragment_to_teacherHomeFragment)
            }
            IDENTIFICATION_DEAN -> {
                setUserIdentification(IDENTIFICATION_DEAN)
                fragment.findNavController()
                    .navigate(R.id.action_verifyFragment_to_deanHomeFragment)
            }
            IDENTIFICATION_PROVOST -> {
                setUserIdentification(IDENTIFICATION_PROVOST)
                fragment.findNavController()
                    .navigate(R.id.action_verifyFragment_to_provostHomeFragment)
            }
        }
    }

    /**
     * 判断当前用户身份，
     * 并导航到对应首页
     */
    fun getUserIdentificationAndNavigateFromStart(
        identification: Int,
        fragment: Fragment
    ) {
        Log.v(
            LOG_TAG,
            "StartFragment getUserIdentificationAndNavigateForStart 当前身份为：$identification"
        )
        when (identification) {
            IDENTIFICATION_STUDENT -> {
                setUserIdentification(IDENTIFICATION_STUDENT)
                fragment.findNavController()
                    .navigate(R.id.action_startFragment_to_studentHomeFragment)
            }
            IDENTIFICATION_TEACHER -> {
                setUserIdentification(IDENTIFICATION_TEACHER)
                fragment.findNavController()
                    .navigate(R.id.action_startFragment_to_teacherHomeFragment)
            }
            IDENTIFICATION_DEAN -> {
                setUserIdentification(IDENTIFICATION_DEAN)
                fragment.findNavController()
                    .navigate(R.id.action_startFragment_to_deanHomeFragment)
            }
            IDENTIFICATION_PROVOST -> {
                setUserIdentification(IDENTIFICATION_PROVOST)
                fragment.findNavController()
                    .navigate(R.id.action_startFragment_to_provostHomeFragment)
            }
        }
    }

    fun loginByUsername(userName: String, pwd: String, callback: (Boolean, String) -> Unit) {
        repository.loginByUsername(userName, pwd, callback)
    }

    fun getUserInfo(callback: (isSuccess: Boolean, user: User?) -> Unit) {
        repository.getUserInfo(callback)
    }

    fun getUserInfoByUsername(
        username: String,
        callback: (isSuccess: Boolean, user: User?, error: String) -> Unit
    ) {
        BmobQuery<User>()
            .addWhereEqualTo(Username, username)
            .findObjects(object : FindListener<User>() {
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null && p0 != null && p0.isNotEmpty()) {
                        callback.invoke(true, p0[0], EMPTY_TEXT)
                        Log.v(LOG_TAG, "找到用户：${p0[0]}")
                    } else {
                        callback.invoke(false, null, p1?.message.toString())
                    }
                }
            })
    }

    /**
     * 获取验证码
     * 请求登录或注册操作的短信验证码
     */
    fun getSignupCode(
        phoneNumber: String,
        callback: (isResponseSuccess: Boolean, msgCode: String, msg: String) -> Unit
    ) {
        repository.getSignupCode(phoneNumber, callback)
    }

    /**
     * 一键注册或登录的同时保存其他字段的数据
     * @param phoneNumber
     * @param msgCode
     */
    fun signOrLogin(
        userName: String,
        workNum: String,
        pwd: String,
        identify: Int,
        phoneNumber: String,
        msgCode: String,
        s: String,
        d: String,
        c: String,
        callback: (isSuccess: Boolean, msg: String) -> Unit
    ) {
        repository.signOrLogin(
            userName, workNum, pwd, identify, phoneNumber, msgCode,
            s, d, c,
            callback
        )
    }

    //退出登录
    fun logout() = BmobUser.logOut()

    fun isLogin(): Boolean = BmobUser.isLogin()

    //手机号码重置密码
    //1. 请求重置密码操作的短信验证码
    fun findPassword(
        phoneNumber: String,
        callback: (isSuccess: Boolean, smsId: Int, error: String?) -> Unit
    ) {
        repository.findPassword(phoneNumber, callback)
    }

    //2. 然后执行验证码的密码重置操作
    fun verifyCode(
        smsId: String,
        newPassword: String,
        callback: (isResetSuccess: Boolean, msg: String) -> Unit
    ) {
        repository.verifyCode(smsId, newPassword, callback)
    }

    /**
     * 模糊查询
     * 查询学校，系
     */
    fun querySchool(
        schoolName: String,
        callback: (isSuccess: Boolean, school: School?, error: String) -> Unit
    ) {
        repository.querySchool(schoolName, callback)
    }

    /**
     * 通过账号/工号/学号判断是否存在该用户
     */
    fun ifExistUserForGivenUsername(
        userName: String,
        callback: (isExist: Boolean, msg: String) -> Unit
    ) {
        BmobQuery<User>()
            .addWhereEqualTo(Username, userName)
            .findObjects(object : FindListener<User>() {
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null && p0 != null && p0.size == 1) {
                        callback.invoke(true, EMPTY_TEXT)
                    } else {
                        callback.invoke(false, "该账户不存在${p1?.message}")
                    }
                }
            })
    }
}
