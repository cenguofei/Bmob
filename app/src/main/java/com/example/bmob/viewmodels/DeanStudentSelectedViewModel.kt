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
import okhttp3.internal.userAgent

class DeanStudentSelectedViewModel(private val handler:SavedStateHandle):ViewModel() {

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
        callback: ((message: String) -> Unit)
    ):
            MutableLiveData<MutableList<DeanStudentSelectedModel>>{
        if (!handler.contains(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA)){
            findStudentWhichHaveSelectedThesis(dean){isSuccess, listThesis, message ->
                if (isSuccess){
                    val resultList = mutableListOf<DeanStudentSelectedModel>()
                    listThesis!!.forEach { thesis->
                        thesis.studentsList?.forEach {student->
                            resultList.add(DeanStudentSelectedModel(thesis.title!!,thesis.teacherName!!,
                                student.studentClass?:"x班级",student.name!!,student.avatarUrl?:""))
                            Log.v(LOG_TAG,"student:$student")
                        }
                    }
                    Log.v(LOG_TAG,"resultList:$resultList")
                    handler.set(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA,resultList)
                }else{
                    callback.invoke(message)
                }
            }
        }
//        if (handler.contains(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA)) {
            return handler.getLiveData(WHICH_HAVE_SELECTED_THESIS_LIVE_DATA)
//        }
//        return MutableLiveData<MutableList<DeanStudentSelectedModel>>()
    }
    /**
     * Thesis表属性：
     *      @see Thesis->studentsList:MutableList<User>
     *      @see Thesis->teacherIsSelectOneStudent:Boolean
     *
     * 只要选择了该课题的学生都找出来给系主任看，教师选择了某一个学生后，
     * 系主任也只能看到一个学生
     */
    private fun findStudentWhichHaveSelectedThesis(
        dean: User,
        callback:(isSuccess:Boolean,listThesis:MutableList<Thesis>?,message:String)->Unit)
    {
        BmobQuery<Thesis>()
            .addWhereEqualTo("school",dean.school)
            .addWhereEqualTo("college",dean.college)
            .addWhereEqualTo("department",dean.department)
            .findObjects(object :FindListener<Thesis>(){
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            Log.v(LOG_TAG,"搜索成功：$p0")
                            callback.invoke(true,p0, com.example.bmob.utils.EMPTY_TEXT)
                        }else{
                            Log.v(LOG_TAG,"findStudentWhichHaveSelectedThesis没有匹配项")
                            callback.invoke(false,null,"没有学生选题信息")
                        }
                    }else{
                        callback.invoke(false,null,"出错了:${p1.message}")
                    }
                }
            })
    }
    companion object{
        private const val WHICH_HAVE_SELECTED_THESIS_LIVE_DATA = "_have_selected_thesis_"
    }
}

data class DeanStudentSelectedModel(
    val thesisTitle:String,
    val thesisTeacherName:String,
    val studentClass:String,
    val studentName:String,
    val studentAvatarUrl:String,
)