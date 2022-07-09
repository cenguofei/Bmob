package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.LOG_TAG

class ApprovedNotApprovedViewModel(private val handler: SavedStateHandle) : ViewModel() {

    private var currentPos = MutableLiveData<Int>()

    fun setCurrentPos(value:Int){
        currentPos.value = value
    }

    fun getCurrentPos() = currentPos.value

    var currentLiveData:MutableLiveData<MutableList<MutableList<Thesis>>> =
        Transformations.switchMap(currentPos){
            if (it == 0){
                handler.getLiveData<MutableList<MutableList<Thesis>>>(APPROVED_THESIS_LIST_LIST)
            }else{
                handler.getLiveData<MutableList<MutableList<Thesis>>>(NOT_APPROVED_THESIS_LIST)
            }
        } as MutableLiveData<MutableList<MutableList<Thesis>>>

    /**
     * 获取未审批的课题
     */
//    fun getNotApprovedThesisList(dean: User, callback: (String) -> Unit): MutableLiveData<MutableList<MutableList<Thesis>>> {
//        if (!handler.contains(NOT_APPROVED_THESIS_LIST)) {
//            queryThesisToDean(dean, NOT_APPROVED) { isSuccess, message ->
//                if (!isSuccess) {
//                    callback.invoke(message)
//                }
//            }
//        }
//        return handler.getLiveData(NOT_APPROVED_THESIS_LIST)
//    }


    /**
     * 获取已审批的课题
     */
//    fun getApprovedThesisList(dean: User, callback: (String) -> Unit):
//            MutableLiveData<MutableList<MutableList<Thesis>>> {
//        if (!handler.contains(APPROVED_THESIS_LIST_LIST)) {
//            queryThesisToDean(dean, ALREADY_APPROVED) { isSuccess, message ->
//                if (!isSuccess) {
//                    callback.invoke(message)
//                }
//            }
//        }
//        return handler.getLiveData(APPROVED_THESIS_LIST_LIST)
//    }

    fun convert(p0: MutableList<Thesis>, approvedState:Int) {
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
        if (approvedState == ALREADY_APPROVED){
            handler.set(APPROVED_THESIS_LIST_LIST, listThesisList)
            Log.v(LOG_TAG,"ALREADY_APPROVED listThesisList:$listThesisList")
        }else{
            Log.v(LOG_TAG,"NOT_APPROVED_THESIS_LIST listThesisList:$listThesisList")
            handler.set(NOT_APPROVED_THESIS_LIST,listThesisList)
        }
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
    fun queryThesisToDeanInApprovedFragment(
        dean: User,
        approvedState:Int,
        callback: (isSuccess: Boolean,data:MutableList<MutableList<Thesis>>?,  message: String) -> Unit
    ) {

        val addWhereEqualToSchool = BmobQuery<Thesis>()
            .addWhereEqualTo("school", dean.school)
        val addWhereEqualToCollege = BmobQuery<Thesis>()
            .addWhereEqualTo("college", dean.college)
        val addWhereEqualToDepartment = BmobQuery<Thesis>()
            .addWhereEqualTo("department", dean.department)
        val addWhereEqualToState = BmobQuery<Thesis>()
            .addWhereEqualTo("thesisState", approvedState)

        val queryList = ArrayList<BmobQuery<Thesis>>().run {
            add(addWhereEqualToSchool)
            add(addWhereEqualToCollege)
            add(addWhereEqualToDepartment)
            add(addWhereEqualToState)
            this@run
        }
        BmobQuery<Thesis>()
            .and(queryList)
            .findObjects(object : FindListener<Thesis>() {
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.isNotEmpty()) {

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

                            callback.invoke(
                                true,
                                listThesisList,
                                ""
                            )
                        } else {
                            callback.invoke(false, null,"没有搜索到相应结果")
                        }
                    } else {
                        callback.invoke(false, null,"请稍后再试:${p1.message}")
                    }
                }
            })
    }
    companion object {
        private const val APPROVED_THESIS_LIST_LIST = "approved_"
        private const val NOT_APPROVED_THESIS_LIST = "not_approved_"
    }
}