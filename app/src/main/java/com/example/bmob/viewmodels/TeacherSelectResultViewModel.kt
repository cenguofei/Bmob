package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.utils.EMPTY_TEXT

class TeacherSelectResultViewModel:ViewModel() {
    private val repository = BmobRepository.getInstance()

    /**
     * 查询选择此课题的学生
     *
     *  user = setViewModel.getUserByQuery()
     *
     * select * from Thesis
     *      where
     *          Thesis.teacherId = user.objectId
     *
     * 选择此课题的学生：
     * selectedStudent = ThesisList[i].studentsList
     */
    fun getStudentOfSelectedThisThesis(callback:(
        isSuccess:Boolean,thesisList:MutableList<SelectedModel>?,msg:String)->Unit){
        repository.getUserInfo{isSuccess, user ->
            if (isSuccess){
                BmobQuery<Thesis>()
                    .addWhereEqualTo("teacherId",user!!.objectId)
                    .findObjects(object :FindListener<Thesis>(){
                        override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                            if (p1 == null){
                                if (p0 != null){
                                    val selectedModelList = mutableListOf<SelectedModel>()
                                    p0.forEach { thesis->
                                        thesis.studentsList?.forEach { student->
                                            selectedModelList.add(SelectedModel(thesis.title!!,student.department!!,student.studentClass ?: "",student.name!!))
                                        }
                                    }
                                    callback.invoke(true,selectedModelList,EMPTY_TEXT)
                                }else{
                                    callback.invoke(false,null,"没有匹配项")
                                }
                            }else{
                                callback.invoke(false,null,p1.message.toString())
                            }
                        }
                    })
            }else{
                callback.invoke(false,null,"查询不到用户信息")
            }
        }
    }
}
data class SelectedModel(
    val title:String,
    val department:String,
    val studentClass:String,
    val studentName:String
)