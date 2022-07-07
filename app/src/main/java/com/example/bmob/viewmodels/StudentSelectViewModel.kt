package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.*
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.utils.LOG_TAG

class StudentSelectViewModel : ViewModel() {
    private val repository = BmobRepository.getInstance()
    private var mutableThesisLiveData = MutableLiveData<MutableList<Thesis>>()

    fun getMutableThesisLiveData() = mutableThesisLiveData

    /**
     * 学生选择自己所在系里面的所有课题
     */
    fun findAllTeacherInDepartment(
        callback: (
            isSuccess: Boolean, teacherList: MutableList<User>?, msg: String
        ) -> Unit
    ) {
        getUserInfo { isSuccess, user ->
            if (isSuccess) {
                //三个条件
                val equalToSchool = BmobQuery<User>()
                    .addWhereEqualTo("school", user!!.school)
                val equalToDepartment = BmobQuery<User>()
                    .addWhereEqualTo("department", user.department)
                val equalToCollege = BmobQuery<User>()
                    .addWhereEqualTo("college", user.college)
                val equalToIdentification = BmobQuery<User>()
                    .addWhereEqualTo("identification", IDENTIFICATION_TEACHER)

                val queryList = ArrayList<BmobQuery<User>>().run {
                    add(equalToSchool)
                    add(equalToDepartment)
                    add(equalToCollege)
                    add(equalToIdentification)
                    this@run
                }

                BmobQuery<User>()
                    .and(queryList)
                    .findObjects(object : FindListener<User>() {
                        override fun done(p0: MutableList<User>?, p1: BmobException?) {
                            if (p1 == null) {
                                if (p0 == null) {
                                    callback.invoke(true, null, EMPTY_SEARCH_RESULT)
                                } else {
                                    callback.invoke(true, p0, EMPTY_MESSAGE)
                                }
                            } else {
                                callback.invoke(false, null, p1.message.toString())
                            }
                        }
                    })
            } else {
                callback.invoke(false, null, "查询不到用户信息")
            }
        }
    }

    private fun getUserInfo(callback: (isSuccess: Boolean, user: User?) -> Unit) {
        repository.getUserInfo(callback)
    }


    /**
     * 查询SelectFragment中args.user
     * 对应的所有课题
     *
     * select * from Thesis
     *      where user.objectId = Thesis.teacherId
     */
    fun getTeacherAllThesis(thesisUser: User, callback: (message: String) -> Unit) {
        BmobQuery<Thesis>()
            .addWhereEqualTo("teacherId", thesisUser.objectId)

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
                        if (p0 == null) {
                            callback.invoke("没有搜索到该教师的课题")
                        } else {
                            mutableThesisLiveData.value = p0
                        }
                    } else {
                        callback.invoke("出错了：${p1.message}")
                    }
                }
            })
    }

    /**
     * 学生选课
     */
    fun addStudentToTeacherThesis(
        student: User,
        thesis: Thesis,
        callback: (isSuccess: Boolean, message: String) -> Unit
    ) {
//        thesis.studentsList?.forEach {
//            if (it.objectId == student.objectId){
//                callback.invoke(null,"已经选择该课题，不能重复选")
//                return
//            }
//        }
        if (student.studentSelectState == STUDENT_HAS_SELECTED_THESIS) {
            callback.invoke(true, "已经选择该课题，不能重复选")
            return
        }
        val thesisStudentList = thesis.studentsList ?: mutableListOf()
        thesisStudentList.add(
            if (thesisStudentList.size == 0) 0 else thesisStudentList.size,
            student
        )
        thesis.studentsList = thesisStudentList
        Log.v(LOG_TAG, "thesisStudentList=$thesisStudentList")

        //更新课题
        thesis.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    callback.invoke(true, "您已成功加入该课题")
                } else {
                    callback.invoke(false, "加入课题失败:${p0.message}")
                }
            }
        })
        //更新学生
        repository.updateUser(STUDENT_HAS_SELECTED_THESIS,student){isSuccess, msg ->
            if (!isSuccess){
                callback.invoke(false,"更新用户信息失败:$msg")
            }
        }
    }

    /**
     * 判断是否为开放时间
     */
    fun isSelectTime(){

    }

    fun addStudentToThesis(
        thesisObjectId: String, studentsList: List<User>,
        callback: (
            isSuccess: Boolean,
            msg: String
        ) -> Unit
    ) {
        val user = User(
            "昵称",
            "https://bmob-cdn-30807.bmobpay.com/2022/07/04/8d2f8b9c40202dd080648e733fa1775c.jpg",
            "https://bmob-cdn-30807.bmobpay.com/2022/07/04/8d2f8b9c40202dd080648e733fa1775c.jpg",
            19,
            "男",
            "1999-11-15",
            "云南",
            2,
            "张三",
            "明天会更好",
            "北京大学",
            "计算机系",
            "xxx院",
            "studentClass",
            false,
            "教师详情"
        )
        Thesis(studentsList = mutableListOf(user, user, user))
            .update("sTZWFFFO", object : UpdateListener() {
                override fun done(p0: BmobException?) {
                    if (p0 == null) {
                        callback.invoke(true, EMPTY_MESSAGE)
                    } else callback.invoke(false, p0.message.toString())
                }
            })
    }
}

const val EMPTY_MESSAGE = ""
const val EMPTY_SEARCH_RESULT = ""