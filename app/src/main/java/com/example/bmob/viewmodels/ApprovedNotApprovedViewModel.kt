package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.LOG_TAG
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ApprovedNotApprovedViewModel(private val handler:SavedStateHandle):ViewModel() {


    fun getApprovedThesisList(dean: User,callback: (String) -> Unit):MutableLiveData<MutableList<Thesis>>{
        if (!handler.contains(APPROVED_THESIS_LIST)){
            queryAllApprovedThesis(dean){isSuccess, thesisList, message ->
                if (isSuccess){
                    handler.set(APPROVED_THESIS_LIST,thesisList)
                }else{
                    callback.invoke(message)
                }
            }
        }
        return if (handler.contains(APPROVED_THESIS_LIST)){
            handler.getLiveData(APPROVED_THESIS_LIST)
        }else{
            MutableLiveData(mutableListOf())
        }
    }

    /**
     * 查询系主任的已审批课题
     *
     * 针对老师,审批状态
     * var thesisState:Int? = null
     */
    private fun queryAllApprovedThesis(dean:User,callback:(isSuccess:Boolean,thesisList:MutableList<Thesis>?,message:String)->Unit){
        BmobQuery<Thesis>()
            .addWhereEqualTo("school",dean.school)
            .addWhereEqualTo("college",dean.college)
            .addWhereEqualTo("department",dean.department)
            .addWhereEqualTo("thesisState", ALREADY_APPROVED)
            .groupby(arrayOf("teacherName"))
            .findObjects(object :FindListener<Thesis>(){
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            callback.invoke(true, p0,com.example.bmob.data.repository.remote.EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null,"没有搜索到相应结果")
                        }
                    }else{
                        callback.invoke(false,null,"请稍后再试:${p1.message}")
                    }
                }
            })
//
//        //三个条件
//        val equalToSchool = BmobQuery<Thesis>()
//            .addWhereEqualTo("school", dean.school)
//        val equalToDepartment = BmobQuery<Thesis>()
//            .addWhereEqualTo("department", dean.department)
//        val equalToCollege = BmobQuery<Thesis>()
//            .addWhereEqualTo("college", dean.college)
//
//        val queryList = ArrayList<BmobQuery<Thesis>>().run {
//            add(equalToSchool)
//            add(equalToDepartment)
//            add(equalToCollege)
//            this@run
//        }
//
//        val map = mutableMapOf<String,JSONObject>()
//
////        val jsonObject = JSONObject()
////        jsonObject.put("",28)
////        map["f"] = jsonObject
//
//        BmobQuery<Thesis>()
//            .and(queryList)
//            .groupby(arrayOf("teacherName"))
//            .findStatistics(Thesis::class.java,object :QueryListener<JSONArray>(){
//                override fun done(p0: JSONArray?, p1: BmobException?) {
//                    if (p1 == null){
//                        Log.v(LOG_TAG,"查询成功：$p0")
//                    }else{
//                        Log.v(LOG_TAG,"查询失败：${p1.message}")
//                    }
//                }
//            })

    }
    /**
     * 查询系主任的未审批课题
     */

    companion object{
        private const val APPROVED_THESIS_LIST = "approved_"
        private const val NOT_APPROVED_THESIS_LIST = "not_approved_"
    }
}