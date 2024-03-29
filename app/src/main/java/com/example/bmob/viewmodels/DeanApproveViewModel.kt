package com.example.bmob.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.*

class DeanApproveViewModel(private val handler: SavedStateHandle) : ViewModel() {
    companion object {
        private const val APPROVED_THESIS_LIST_LIST = "_approved_"
        private const val NOT_APPROVED_THESIS_LIST = "_not_approved_"
    }

    fun convert(p0: MutableList<Thesis>, approvedState: Int) {
        val hashMapOf = hashMapOf<String, MutableList<Thesis>>()
        p0.forEach {
            if (!hashMapOf.contains(it.teacherName!!)) {
                val mutableListOf = mutableListOf<Thesis>()
                mutableListOf.add(it)
                hashMapOf[it.teacherName!!] = mutableListOf
            } else {
                hashMapOf[it.teacherName]!!.add(it)
            }
        }
        val listThesisList: MutableList<MutableList<Thesis>> = mutableListOf()
        hashMapOf.values.forEach {
            listThesisList.add(it)
        }
        if (approvedState == ALREADY_APPROVED) {
            handler.set(APPROVED_THESIS_LIST_LIST, listThesisList)
        } else {
            handler.set(NOT_APPROVED_THESIS_LIST, listThesisList)
        }
    }

    /**
     * 获取已选该课题的学生名单
     */
    fun getQueryThesisToDeanNotApprovedLiveData(
        dean: User,
        approvedState: Int,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<MutableList<Thesis>>> {
        if (!handler.contains(NOT_APPROVED_THESIS_LIST)) {
            queryThesisToDeanInApprovedFragment(dean, approvedState) { isSuccess, data, message ->
                if (isSuccess) {
                    handler.set(NOT_APPROVED_THESIS_LIST, data)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(NOT_APPROVED_THESIS_LIST)
    }

    /**
     * 获取未选该课题的学生名单
     */
    fun getQueryThesisToDeanApprovedLiveData(
        dean: User,
        approvedState: Int,
        callback: (message: String) -> Unit
    ): MutableLiveData<MutableList<MutableList<Thesis>>> {
        if (!handler.contains(APPROVED_THESIS_LIST_LIST)) {
            queryThesisToDeanInApprovedFragment(dean, approvedState) { isSuccess, data, message ->
                if (isSuccess) {
                    handler.set(APPROVED_THESIS_LIST_LIST, data)
                } else {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(APPROVED_THESIS_LIST_LIST)
    }

    /**
     * 查询系主任的已审批课题
     *
     * 针对老师,审批状态
     * var thesisState:Int? = null
     *
     * @param approvedState
     * ALREADY_APPROVED  已经审批
     * NOT_APPROVED 还没审批
     * THESIS_APPROVED_REJECTED = 2 审批未通过
     */
    private fun queryThesisToDeanInApprovedFragment(
        dean: User,
        approvedState: Int,
        callback: (isSuccess: Boolean, data: MutableList<MutableList<Thesis>>?, message: String) -> Unit
    ) {
        val queryList = ArrayList<BmobQuery<Thesis>>().apply {
            add(BmobQuery<Thesis>().addWhereEqualTo(School, dean.school))
            add(BmobQuery<Thesis>().addWhereEqualTo(College, dean.college))
            add(BmobQuery<Thesis>().addWhereEqualTo(Department, dean.department))
            add(BmobQuery<Thesis>().addWhereEqualTo(ThesisState, approvedState))
        }
        BmobQuery<Thesis>()
            .and(queryList)
            .findObjects(object : FindListener<Thesis>() {
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            callback.invoke(true, convertToDoubleList(p0), EMPTY_TEXT)
                        } else {
                            callback.invoke(false, null, "没有搜索到相应结果")
                        }
                    } else {
                        callback.invoke(false, null, "请稍后再试:${p1.message}")
                    }
                }
            })
    }

    private fun convertToDoubleList(thesisList: MutableList<Thesis>): MutableList<MutableList<Thesis>> {
        val hashMapOf = hashMapOf<String, MutableList<Thesis>>()
        thesisList.forEach {
            if (!hashMapOf.contains(it.teacherName!!)) {
                val mutableListOf = mutableListOf<Thesis>()
                mutableListOf.add(it)
                hashMapOf[it.teacherName!!] = mutableListOf
            } else {
                hashMapOf[it.teacherName]!!.add(it)
            }
        }
        val listThesisList: MutableList<MutableList<Thesis>> = mutableListOf()
        hashMapOf.values.forEach {
            listThesisList.add(it)
        }
        return listThesisList
    }


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