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
import com.example.bmob.utils.*

class ProvostSkimViewModel(val handler:SavedStateHandle):ViewModel() {
    companion object{
        private const val SKIM_TEACHER_KEY = "_skim_teacher_"
        private const val SKIM_STUDENT_KEY = "_skim_student_"
        private const val SKIM_DEAN_KEY = "_skim_dean_"
    }

    /**
     * 获取学生的信息
     */
    fun getStudentInfoLiveData(provost: User,callback:(message:String)->Unit):MutableLiveData<MutableList<User>>{
        if (!handler.contains(SKIM_STUDENT_KEY)){
            getUserInfo(provost, IDENTIFICATION_STUDENT){ isSuccess, userList, message ->
                if (isSuccess){
                    Log.v(LOG_TAG,"学生信息为空，设置：$userList")
                    handler.set(SKIM_STUDENT_KEY,userList)
                }else{
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(SKIM_STUDENT_KEY)
    }

    /**
     * 获取教师信息
     */
    fun getTeacherInfoLiveData(provost: User,callback:(message:String)->Unit):MutableLiveData<MutableList<User>>{
        if (!handler.contains(SKIM_TEACHER_KEY)){
            getUserInfo(provost, IDENTIFICATION_TEACHER){ isSuccess, userList, message ->
                if (isSuccess){
                    Log.v(LOG_TAG,"教师信息为空，设置：$userList")
                    handler.set(SKIM_TEACHER_KEY,userList)
                }else{
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
        callback:(message:String)->Unit
    ):MutableLiveData<MutableList<User>>{
        if (!handler.contains(SKIM_DEAN_KEY)){
            getUserInfo(provost, IDENTIFICATION_DEAN){ isSuccess, userList, message ->
                if (isSuccess){
                    Log.v(LOG_TAG,"系主任信息为空，设置：$userList")
                    handler.set(SKIM_DEAN_KEY,userList)
                }else{
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(SKIM_DEAN_KEY)
    }

    /**
     * 获取处教务长意外的角色信息
     * @param provost
     * @param identification  用户身份
     */
    private fun getUserInfo(
        provost:User,
        identification:Int,
        callback:(isSuccess:Boolean,userList:MutableList<User>?,message:String)->Unit
    ){
        val addWhereEqualToSchool = BmobQuery<User>()
            .addWhereEqualTo(School, provost.school)
        val addWhereEqualToCollege = BmobQuery<User>()
            .addWhereEqualTo(College, provost.college)
        val addWhereEqualToDepartment = BmobQuery<User>()
            .addWhereEqualTo(Department, provost.department)
        val addWhereEqualToIdentification = BmobQuery<User>()
            .addWhereEqualTo(Identification, identification)


        val queryList = ArrayList<BmobQuery<User>>().run {
            add(addWhereEqualToSchool)
            add(addWhereEqualToCollege)
            add(addWhereEqualToDepartment)
            add(addWhereEqualToIdentification)
            this@run
        }

        BmobQuery<User>()
            .and(queryList)
            .findObjects(object :FindListener<User>(){
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            callback.invoke(true,p0, EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null,"没有搜索到数据")
                        }
                    }else{
                        callback.invoke(false,null,p1.message.toString())
                    }
                }
            })
    }
}