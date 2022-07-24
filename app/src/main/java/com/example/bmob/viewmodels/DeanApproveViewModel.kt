package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.Thesis

class DeanApproveViewModel : ViewModel() {

    /**
     * 统一教师的课题上传申请
     */
    fun updateThesisForDeanApprove(
        agreeThesis: Thesis,
        thesisState: Int,
        callback: (isSuccess: Boolean, message: String) -> Unit
    ) {
        agreeThesis.thesisState = thesisState
        agreeThesis.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    callback(true, "操作成功")
                } else {
                    callback.invoke(false, "更新失败:${p0.message}")
                }
            }
        })
    }
}