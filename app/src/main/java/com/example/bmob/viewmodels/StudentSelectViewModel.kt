package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.*
import com.example.bmob.utils.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StudentSelectViewModel(private val handler: SavedStateHandle) : ViewModel() {
    var isStudentSelectThesis = MutableLiveData<Boolean>()
    var releaseTime: ReleaseTime? = null

    companion object {
        private const val TEACHER_IN_DEPARTMENT_KEY = "_teacher_in_depart_"
        private const val MUTABLE_THESIS_KEY = "_mu_thesis_"
    }

    /**
     * @param teacher 某个老师的账号
     * 获取学生所在系的老师的所有课题
     */
    fun getMutableTeacherThesisLiveData(
        teacher: User,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<Thesis>> {
        if (!handler.contains(MUTABLE_THESIS_KEY+teacher.objectId)) {
            getTeacherAllThesis(teacher) { isSuccess, thesisList, message ->
                if (isSuccess) {
                    handler.set(MUTABLE_THESIS_KEY+teacher.objectId, thesisList)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(MUTABLE_THESIS_KEY+teacher.objectId)
    }

    /**
     * @param student 当前登录的学生账号
     * 返回student所在系的所有老师
     */
    fun getAllTeacherInDepartmentLiveData(
        student: User,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<User>> {
        if (!handler.contains(TEACHER_IN_DEPARTMENT_KEY)) {
            findAllTeacherInDepartment(student) { isSuccess, teacherList, msg ->
                if (isSuccess) {
                    handler.set(TEACHER_IN_DEPARTMENT_KEY, teacherList)
                } else {
                    callback.invoke(msg)
                }
            }
        }
        return handler.getLiveData(TEACHER_IN_DEPARTMENT_KEY)
    }

    /**
     * 学生选择自己所在系里面的所有课题
     */
    private fun findAllTeacherInDepartment(
        student: User,
        callback: (
            isSuccess: Boolean, teacherList: MutableList<User>?, msg: String
        ) -> Unit
    ) {
        val queryList = ArrayList<BmobQuery<User>>().apply {
            add(BmobQuery<User>()
                .addWhereEqualTo(School, student.school))
            add(BmobQuery<User>()
                .addWhereEqualTo(Department, student.department))
            add(BmobQuery<User>()
                .addWhereEqualTo(College, student.college))
            add(BmobQuery<User>()
                .addWhereEqualTo(Identification, IDENTIFICATION_TEACHER))
        }
        BmobQuery<User>()
            .and(queryList)
            .findObjects(object : FindListener<User>() {
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 == null) {
                            callback.invoke(false, null, EMPTY_TEXT)
                        } else {

                            callback.invoke(true, p0, EMPTY_TEXT)
                        }
                    } else {
                        callback.invoke(false, null, p1.message.toString())
                    }
                }
            })
    }

    /**
     * 查询SelectFragment中args.user
     * 对应的所有课题
     *
     * select * from Thesis
     *      where user.objectId = Thesis.teacherId
     */
    private fun getTeacherAllThesis(
        thesisUser: User,
        callback: (isSuccess: Boolean, thesisList: MutableList<Thesis>?, message: String) -> Unit
    ) {
        BmobQuery<Thesis>()
            .addWhereEqualTo(TeacherId, thesisUser.objectId)
            /**
             * 这里还要添加条件
             * 等系主任
             * 等教务长功能完善后再依次更改
             *
             *
             *     //针对学生 ，论文是否可选，当审核通过并且选题时间开始后为true，表示可选
             *     var enabledToStudent:Boolean? = null,
             *     //针对老师,审批状态
             *     var thesisState:Int? = null
             */
            .findObjects(object : FindListener<Thesis>() {
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            Log.v(LOG_TAG, "搜索成功p0=$p0")
                            callback.invoke(true, p0, EMPTY_TEXT)
                        } else {
                            callback.invoke(false, null, "没有搜索到该教师的课题")
                        }
                    } else {
                        callback.invoke(false, null, "出错了：${p1.message}")
                    }
                }
            })
    }


    fun fetchStudentThesis(student: User,callback: (student: User) -> Unit){
        BmobQuery<User>()
            .addWhereEqualTo(ObjectId,student.objectId)
            .include(StudentThesis)
            .setLimit(1)
            .findObjects(object :FindListener<User>(){
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null && p0 != null && p0.isNotEmpty()){
                        Log.v(LOG_TAG,"更新学生成功：${p0[0]}")
                        callback.invoke(p0[0])
                    }else{
                        callback.invoke(student)
                    }
                }
            })
    }

    /**
     * 学生选课,把当前登录的学生账号信息添加到Thesis.studentsList
     */
    fun addStudentToTeacherThesis(
        student: User,
        thesis: Thesis,
        messageCallback: (message: String) -> Unit,
        updateStudentCallback: (student: User) -> Unit
    ) {
        viewModelScope.launch {
//            if (student.studentSelectState == STUDENT_HAS_SELECTED_THESIS) {
//                messageCallback.invoke("已经选择课题，不能多选或重复选")
//            } else {
            val thesisStudentList = thesis.studentsList ?: mutableListOf()
            thesisStudentList.add(student)
            /**
             * student.studentThesis = thesis
             * 上面的写法时错误的，会闪退，找了很久也没找到原因
             */
            student.studentSelectState = STUDENT_HAS_SELECTED_THESIS
            student.isAgree = false
            val thesis1 = Thesis()
            Log.v(LOG_TAG, "thesis=$thesis")
            thesis1.objectId = thesis.objectId
            student.studentThesis = thesis1
            student.update(student.objectId, object : UpdateListener() {
                override fun done(p0: BmobException?) {
                    if (p0 == null) {
                        updateStudentCallback.invoke(student)
                    } else {
                        messageCallback.invoke("更新用户信息失败:${p0.message.toString()}")
                    }
                }
            })

            thesis.studentsList = thesisStudentList
            Log.v(LOG_TAG, "thesisStudentList=$thesisStudentList")

            //更新课题
            thesis.update(object : UpdateListener() {
                override fun done(p0: BmobException?) {
                    if (p0 == null) {
                        messageCallback.invoke("您已成功选择该课题")
                    } else {
                        messageCallback.invoke("加入课题失败:${p0.message}")
                    }
                }
            })
        }
    }

    /**
     * 查询选题时间
     */
    fun queryChooseThesisTime(
        student: User,
        callback: (isSuccess: Boolean, releaseTime: ReleaseTime?, message: String) -> Unit
    ) {
        BmobQuery<ReleaseTime>()
            .addWhereEqualTo(School, student.school)
            .setLimit(1)
            .findObjects(object : FindListener<ReleaseTime>() {
                override fun done(p0: MutableList<ReleaseTime>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            if (timeIsInDate(p0[0].endTime)) {
                                releaseTime = p0[0]
                                callback.invoke(true, p0[0], EMPTY_TEXT)
                            } else {
                                callback.invoke(false, null, "发布的选题时间已过期")
                            }
                        } else {
                            callback.invoke(false, null, "还没有教务长发布选题时间，请耐心等待")
                        }
                    } else {
                        callback.invoke(false, null, "查询选题时间失败:${p1.message}")
                    }
                }
            })
    }

    /**
     * 判断时间是否过期
     */
    private fun timeIsInDate(endTime: String): Boolean {
        return try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val dateEnd = simpleDateFormat.parse(endTime)

            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH) + 1
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val dateSystem = simpleDateFormat.parse("$year-$month-$day $hour:00:00")

            if (dateSystem != null && dateEnd != null) {
                return !dateEnd.before(dateSystem)
//                if (dateEnd.before(dateSystem)){
//                    return false
//                }else{
//                    return true
//                }
            } else {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 当学生点击加入按钮时，判断当前时间是否在选题时间内
     */
    fun isInSelectTime(releaseTime: ReleaseTime): Boolean {
        return try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val dateStart = simpleDateFormat.parse(releaseTime.beginTime)
            val dateEnd = simpleDateFormat.parse(releaseTime.endTime)
            Log.v(LOG_TAG, "开始：${releaseTime.beginTime}  结束：${releaseTime.endTime}")

            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH) + 1
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val minute: Int = calendar.get(Calendar.MINUTE)
            val second: Int = calendar.get(Calendar.SECOND)

            Log.v(LOG_TAG, "当前时间：$year-$month-$day $hour:$minute:$second")

            val dateSystem = simpleDateFormat.parse("$year-$month-$day $hour:$minute:$second")

            if (dateSystem != null && dateStart != null && dateEnd != null) {
                return dateSystem.after(dateStart) && dateSystem.before(dateEnd)
//                if (dateSystem.after(dateStart) && dateSystem.before(dateEnd)){
//                    return true
//                }else return false
            } else return false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}