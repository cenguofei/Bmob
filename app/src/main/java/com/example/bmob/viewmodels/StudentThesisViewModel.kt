package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.STUDENT_NOT_SELECT_THESIS
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.LOG_TAG

class StudentThesisViewModel : ViewModel() {

    /**
     * 学生退选
     */
    fun studentOutThesis(
        student: User,
        callback: (isSuccess: Boolean, student: User?, message: String) -> Unit
    ) {
        student.studentSelectState = STUDENT_NOT_SELECT_THESIS
        /**
         * 先更新学生已选的课题信息
         */
        student.studentThesis?.let {
            updateThesisAfterStudentOutSelect(it, student)
        }
        student.studentThesis = null
        student.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    callback.invoke(true, student, "已成功退选")
                } else {
                    callback.invoke(false, null, "${p0.message}")
                }
            }
        })
    }

    private fun updateThesisAfterStudentOutSelect(thesis: Thesis, student: User) {
        val studentsList = thesis.studentsList
        studentsList?.forEach {
            if (it.objectId == student.objectId) {
                //移除该学生
                studentsList.remove(it)
                thesis.selectedNum = thesis.selectedNum!! - 1
            }
        }
        thesis.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 != null) {
                    Log.v(LOG_TAG, "学生退选时更新课题失败：${p0.message}")
                }
            }
        })
    }
}