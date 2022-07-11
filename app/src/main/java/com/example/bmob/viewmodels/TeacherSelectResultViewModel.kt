package com.example.bmob.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.EMPTY_TEXT
import com.example.bmob.utils.TeacherId

class TeacherSelectResultViewModel(private val handler: SavedStateHandle) : ViewModel() {

    companion object {
        private const val STUDENT_SELECTED_THIS_THESIS_KEY = "_stu_s_thesis_"
    }

    fun getStudentOfSelectedThisThesisLiveData(
        teacher: User,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<SelectedModel>> {
        if (!handler.contains(STUDENT_SELECTED_THIS_THESIS_KEY)) {
            getStudentOfSelectedThisThesis(teacher) { isSuccess, thesisList, msg ->
                if (isSuccess) {
                    handler.set(STUDENT_SELECTED_THIS_THESIS_KEY, thesisList)
                } else {
                    callback.invoke(msg)
                }
            }
        }
        return handler.getLiveData(STUDENT_SELECTED_THIS_THESIS_KEY)
    }

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
     * selectedStudent = ThesisList{i}.studentsList
     */
    private fun getStudentOfSelectedThisThesis(
        teacher: User,
        callback: (isSuccess: Boolean, thesisList: MutableList<SelectedModel>?, msg: String) -> Unit
    ) {
        BmobQuery<Thesis>()
            .addWhereEqualTo(TeacherId, teacher.objectId)
            .findObjects(object : FindListener<Thesis>() {
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null) {
                            val selectedModelList = mutableListOf<SelectedModel>()
                            p0.forEach { thesis ->
                                thesis.studentsList?.forEach { student ->
                                    selectedModelList.add(
                                        SelectedModel(
                                            thesis.title!!,
                                            student.department!!,
                                            student.studentClass ?: "",
                                            student.name!!
                                        )
                                    )
                                }
                            }
                            callback.invoke(true, selectedModelList, EMPTY_TEXT)
                        } else {
                            callback.invoke(false, null, "没有匹配项")
                        }
                    } else {
                        callback.invoke(false, null, p1.message.toString())
                    }
                }
            })
    }
}

data class SelectedModel(
    val title: String,
    val department: String,
    val studentClass: String,
    val studentName: String
)