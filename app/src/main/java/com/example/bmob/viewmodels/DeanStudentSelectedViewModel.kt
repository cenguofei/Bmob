package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.LOG_TAG
import java.util.ArrayList

class DeanStudentSelectedViewModel(private val handler: SavedStateHandle) : ViewModel() {

    companion object {
        private const val WHICH_HAVE_SELECTED_THESIS_LIVE_DATA = "_have_selected_thesis_"
        private const val WHICH_NOT_SELECT_ONE_THESIS = "_not_select_one_"
    }

    /**
     * 查询已经选择课题的本系的学生
     *
     * 查询没有选择课题的本系的学生
     *
     *
     * 1. 导出选题结果
     * 系主任通过
     *      Thesis的属性：学校school  院college  系department  找到系里面的老师
     *              需要传入系主任的信息，即
     *              select * from Thesis
     *                      where Thesis.school = dean.school
     *                       and Thesis.college = dean.college
     *                       and Thesis.department = dean.department
     *
     *
     * 然后通过老师的objectId(系统属性) 找到属于这个老师的所有Thesis
     *
     * 最后，得到 studentsList
     *
     * 2. 查看未选学生名单
     * 系主任通过 学校school  院college  系department 找到该系里面所有学生的名单
     *
     * 然后根据User.studentSelectState这个属性筛选出未选学生名单
     *
     * 然后按照班级分类
     */
    fun getStudentWhichHaveSelectedThesisLiveData(
        dean: User,
        studentSelectState: Boolean,
        callback: ((message: String) -> Unit)
    ): MutableLiveData<MutableList<DeanStudentSelectedModel>> {
        if (!handler.contains(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA)) {
            findStudentsWhichHaveSelectedThesis(
                dean,
                studentSelectState
            ) { isSuccess, listThesis, message ->
                if (isSuccess) {
                    Log.v(LOG_TAG,"findStudentsWhichHaveSelectedThesis isSuccess")
                    val resultList = mutableListOf<DeanStudentSelectedModel>()
                    listThesis!!.forEach { thesis ->
                        thesis.studentsList?.forEach { student ->
                            resultList.add(
                                DeanStudentSelectedModel(
                                    thesis.title!!,
                                    thesis.teacherName!!,
                                    student.studentClass ?: "x班级",
                                    student.name!!,
                                    student.avatarUrl ?: ""
                                )
                            )
                        }
                    }
                    Log.v(LOG_TAG,"resultList:$resultList")
                    handler.set(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA, resultList)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA)
    }

    /**
     * Thesis表属性：
     *      @see Thesis->studentsList:MutableList<User>
     *      @see Thesis->teacherIsSelectOneStudent:Boolean
     *
     * 只要选择了该课题的学生都找出来给系主任看，教师选择了某一个学生后，
     * 系主任也只能看到一个学生
     */
    private fun findStudentsWhichHaveSelectedThesis(
        dean: User,
        studentSelectState: Boolean,
        callback: (isSuccess: Boolean, listThesis: MutableList<Thesis>?, message: String) -> Unit
    ) {
        val addWhereEqualTo = BmobQuery<Thesis>().addWhereEqualTo("school", dean.school)
        val addWhereEqualTo1 = BmobQuery<Thesis>().addWhereEqualTo("college", dean.college)
        val addWhereEqualTo2 = BmobQuery<Thesis>().addWhereEqualTo("department", dean.department)
        val addWhereEqualTo3 =
            BmobQuery<Thesis>().addWhereEqualTo("studentSelectState", studentSelectState)

        val queryList = ArrayList<BmobQuery<Thesis>>()
        queryList.add(addWhereEqualTo)
        queryList.add(addWhereEqualTo1)
        queryList.add(addWhereEqualTo2)


        BmobQuery<Thesis>()
            .and(queryList)
            .findObjects(object : FindListener<Thesis>() {
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            Log.v(LOG_TAG, "搜索成功：$p0")
                            callback.invoke(true, p0, com.example.bmob.utils.EMPTY_TEXT)
                        } else {
                            Log.v(LOG_TAG, "findStudentWhichHaveSelectedThesis没有匹配项")
                            callback.invoke(false, null, "没有学生选题信息")
                        }
                    } else {
                        callback.invoke(false, null, "出错了:${p1.message}")
                    }
                }
            })
    }

    fun getStudentsWhichNotSelectedThesisLiveData(
        dean: User,
        studentSelectState: Boolean,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<User>> {
        if (!handler.contains(WHICH_NOT_SELECT_ONE_THESIS)) {
            findStudentsWhichNotSelectedThesis(
                dean,
                studentSelectState
            ) { isSuccess, data, message ->
                if (isSuccess) {
                    handler.set(WHICH_NOT_SELECT_ONE_THESIS, data)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(WHICH_NOT_SELECT_ONE_THESIS)
    }

    private fun findStudentsWhichNotSelectedThesis(
        dean: User,
        studentSelectState: Boolean,
        callback: (isSuccess: Boolean, data: MutableList<User>?, message: String) -> Unit
    ) {
        val addWhereEqualTo = BmobQuery<User>().addWhereEqualTo("school", dean.school)
        val addWhereEqualTo1 = BmobQuery<User>().addWhereEqualTo("college", dean.college)
        val addWhereEqualTo2 = BmobQuery<User>().addWhereEqualTo("department", dean.department)
        BmobQuery<User>().addWhereEqualTo("studentSelectState", studentSelectState)

        val queryList = ArrayList<BmobQuery<User>>()
        queryList.add(addWhereEqualTo)
        queryList.add(addWhereEqualTo1)
        queryList.add(addWhereEqualTo2)

        BmobQuery<User>()
            .and(queryList)
            .findObjects(object : FindListener<User>() {
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            callback.invoke(true, p0, "")
                        } else {
                            callback.invoke(false, null, "没有找到相关数据")
                        }
                    } else {
                        callback.invoke(false, null, "异常:${p1.message}")
                    }
                }
            })
    }
}

data class DeanStudentSelectedModel(
    val thesisTitle: String,
    val thesisTeacherName: String,
    val studentClass: String,
    val studentName: String,
    val studentAvatarUrl: String,
)