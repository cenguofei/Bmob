package com.example.bmob.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User

class ApprovedNotApprovedViewModel(private val handler: SavedStateHandle) : ViewModel() {


    fun getApprovedThesisList(dean: User, callback: (String) -> Unit):
            MutableLiveData<MutableList<MutableList<Thesis>>> {
        if (!handler.contains(APPROVED_THESIS_LIST_LIST)) {
            queryAllNotSkimThesis(dean, ALREADY_APPROVED) { isSuccess, message ->
                if (!isSuccess) {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(APPROVED_THESIS_LIST_LIST)
    }

    fun convert(p0: MutableList<Thesis>) {
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
        handler.set(APPROVED_THESIS_LIST_LIST, listThesisList)
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
    private fun queryAllNotSkimThesis(
        dean: User,
        approvedState:Int,
        callback: (isSuccess: Boolean, message: String) -> Unit
    ) {
        BmobQuery<Thesis>()
            .addWhereEqualTo("school", dean.school)
            .addWhereEqualTo("college", dean.college)
            .addWhereEqualTo("department", dean.department)
            .addWhereEqualTo("thesisState", approvedState)
            .findObjects(object : FindListener<Thesis>() {
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {
                            convert(p0)
                            callback.invoke(
                                true,
                                com.example.bmob.data.repository.remote.EMPTY_TEXT
                            )
                        } else {
                            callback.invoke(false, "没有搜索到相应结果")
                        }
                    } else {
                        callback.invoke(false, "请稍后再试:${p1.message}")
                    }
                }
            })
    }

    /**
     * 查询系主任的未审批课题
     */
    fun getNotApprovedThesisList(dean: User, callback: (String) -> Unit)
            : MutableLiveData<MutableList<MutableList<Thesis>>> {
        if (!handler.contains(NOT_APPROVED_THESIS_LIST)) {
            queryAllNotSkimThesis(dean, NOT_APPROVED) { isSuccess, message ->
                if (!isSuccess) {
                    callback.invoke(message)
                }
            }
        }
        return handler.getLiveData(NOT_APPROVED_THESIS_LIST)
    }

    companion object {
        private const val APPROVED_THESIS_LIST_LIST = "approved_"
        private const val NOT_APPROVED_THESIS_LIST = "not_approved_"
    }
}