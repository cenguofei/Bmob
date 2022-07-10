package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.ReleaseTime
import com.example.bmob.data.entity.User
import com.example.bmob.utils.EMPTY_TEXT
import com.example.bmob.utils.LOG_TAG
import java.text.SimpleDateFormat
import java.util.*

class ProvostViewModel(private val handler:SavedStateHandle):ViewModel() {

    fun getProvostReleaseSelectTimeLiveData(provost: User):MutableLiveData<ReleaseTime>{
        if (!handler.contains(PROVOST_RELEASE_SELECT_TIME)){
            queryIssuedReleaseTime(provost){
                handler.set(PROVOST_RELEASE_SELECT_TIME,it)
            }
        }
        return handler.getLiveData(PROVOST_RELEASE_SELECT_TIME)
    }

    fun saveTime(
        beginTime: String,
        endTime: String,
        provost:User,
        callback: (isSuccess: Boolean, message: String) -> Unit
    ){
        ReleaseTime(
            provost.objectId,
            provost.name!!,
            provost.school!!,
            beginTime = beginTime,
            endTime = endTime,
            true
        ).run {
            save(object :SaveListener<String>(){
                override fun done(p0: String?, p1: BmobException?) {
                    if (p1 == null){
                        callback.invoke(true, "保存成功")
                        handler.set(PROVOST_RELEASE_SELECT_TIME,this@run)
                    }else{
                        callback.invoke(false,p1.message.toString())
                    }
                }
            })
        }
    }


    fun checkIsEndValid(
        beginTime:String,
        endTime:String,
        callback: (isValid: Boolean,message:String) -> Unit
    ){

        if (beginTime == EMPTY_TEXT || endTime == EMPTY_TEXT){
            callback.invoke(false,"请输入时间")
            return
        }

        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val dateBegin = simpleDateFormat.parse(beginTime)
            val dateEnd = simpleDateFormat.parse(endTime)

            //开始时间不能大于结束时间
            if (dateBegin != null) {
                if (dateBegin.after(dateEnd)){
                    callback.invoke(false,"开始时间不能大于结束时间")
                    return
                }
            }

            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH) + 1
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val dateSystem = simpleDateFormat.parse("$year-$month-$day $hour:00:00")

            //开始时间不能小于系统当前时间
            if (dateSystem != null) {
                if (dateSystem.after(dateBegin)){
                    callback.invoke(false,"开始时间不能小于系统当前时间")
                    return
                }
            }
        }catch (e:Exception){
            callback.invoke(false,"系统故障")
            e.printStackTrace()
        }
        callback.invoke(true, EMPTY_TEXT)
    }

    private fun queryIssuedReleaseTime(provost: User,callback:(release:ReleaseTime)->Unit) {
        val addWhereEqualTo1 = BmobQuery<ReleaseTime>()
            .addWhereEqualTo("provostObjectId", provost.objectId)
        val addWhereEqualTo2 = BmobQuery<ReleaseTime>()
            .addWhereEqualTo("school", provost.school)

        val queryList = ArrayList<BmobQuery<ReleaseTime>>().run {
            add(addWhereEqualTo1)
            add(addWhereEqualTo2)
            this@run
        }

        BmobQuery<ReleaseTime>()
            .and(queryList)
            .findObjects(object :FindListener<ReleaseTime>(){
                override fun done(
                    p0: MutableList<ReleaseTime>?,
                    p1: BmobException?
                ){
                    if (p1 == null && p0 != null && p0.isNotEmpty()){
                        callback.invoke(p0[0])
                    }
                }
            })
    }

    fun updateReleaseTime(
        release: ReleaseTime,
        beginTime: String,
        endTime: String,
        callback: (message: String) -> Unit
    ) {
        /**
         * {"data":{},"result":{"code":103,"message":"c is null."}}
         *
         * 不能使用release.update()来更新，否则会报以上错误，需要新建对象
         */
        val releaseTime =
            ReleaseTime(
                release.provostObjectId,
                release.provostName,
                release.school,
                beginTime,
                endTime,
                true
            )
        release.beginTime = beginTime
        release.endTime = endTime
        releaseTime.update(release.objectId,object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0 == null){
                    callback.invoke("更新成功")
                }else{
                    Log.v(LOG_TAG,"更新失败：${p0.message}")
                    callback.invoke("更新失败:${p0.message}")
                }
            }
        })
    }

    companion object{
        private const val PROVOST_RELEASE_SELECT_TIME = "_pro_rel_"
    }
}