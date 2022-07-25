package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.entity.User
import com.example.bmob.utils.EMPTY_TEXT
import com.example.bmob.utils.Identification
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.School

class ProvostSkimViewModel(val handler: SavedStateHandle) : ViewModel() {

    /**
     * 获取学生的信息
     */
    fun getStudentInfoLiveData(
        provost: User,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<User>> {
        if (!handler.contains(SKIM_STUDENT_KEY)) {
            getUserInfo(provost, IDENTIFICATION_STUDENT) { isSuccess, userList, message ->
                if (isSuccess) {
                    handler.set(SKIM_STUDENT_KEY, userList)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(SKIM_STUDENT_KEY)
    }

    /**
     * 获取教师信息
     */
    fun getTeacherInfoLiveData(
        provost: User,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<User>> {
        if (!handler.contains(SKIM_TEACHER_KEY)) {
            getUserInfo(provost, IDENTIFICATION_TEACHER) { isSuccess, userList, message ->
                if (isSuccess) {
                    handler.set(SKIM_TEACHER_KEY, userList)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(SKIM_TEACHER_KEY)
    }

    /**
     * 获取教师信息
     */
    fun getDeanInfoLiveData(
        provost: User,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<User>> {
        if (!handler.contains(SKIM_DEAN_KEY)) {
            getUserInfo(provost, IDENTIFICATION_DEAN) { isSuccess, userList, message ->
                if (isSuccess) {
                    handler.set(SKIM_DEAN_KEY, userList)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(SKIM_DEAN_KEY)
    }

    /**
     * 获取除教务长意外的角色信息
     * @param provost
     * @param identification  用户身份
     */
    private fun getUserInfo(
        provost: User,
        identification: Int,
        callback: (isSuccess: Boolean, userList: MutableList<User>?, message: String) -> Unit
    ) {
        val queryList = ArrayList<BmobQuery<User>>().run {
            add(
                BmobQuery<User>()
                    .addWhereEqualTo(School, provost.school)
            )
            add(
                BmobQuery<User>()
                    .addWhereEqualTo(Identification, identification)
            )
            this@run
        }
        BmobQuery<User>()
            .and(queryList)
            .findObjects(object : FindListener<User>() {
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            callback.invoke(true, p0, EMPTY_TEXT)
                        } else {
                            callback.invoke(false, null, "没有搜索到数据")
                        }
                    } else {
                        callback.invoke(false, null, p1.message.toString())
                    }
                }
            })
    }

    companion object {
        private const val SKIM_TEACHER_KEY = "_skim_teacher_"
        private const val SKIM_STUDENT_KEY = "_skim_student_"
        private const val SKIM_DEAN_KEY = "_skim_dean_"
    }
}