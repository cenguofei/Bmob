package com.example.bmob.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.data.entity.BmobBannerObject
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.data.repository.remote.EMPTY_TEXT
import com.example.bmob.utils.LOG_TAG

class StudentHomeViewModel:ViewModel() {
    private val repository = BmobRepository.getInstance()

    var queryThesisListLiveData = MutableLiveData<List<Thesis>>()


//    var queryThesisByInputContent = Transformations.switchMap(nowQueryContent){
//        Log.v(LOG_TAG,"queryThesisByInputContent接受到nowQueryContent通知：nowQueryContent.value = $it")
//        var data:List<Thesis>? = null
//        searchAnyThesis(it){isSuccess, thesis, msg ->
//            data = if (isSuccess){
//                thesis
//            }else{
//                null
//            }
//        }
//        Log.v(LOG_TAG,"queryThesisByInputContent设置值：$data")
//        MutableLiveData<List<Thesis>>(data)
//    }

    /**
     * 搜索学生首页的banner
     */
    fun queryBannerData(callback: (isSuccess: Boolean, data: MutableList<BmobBannerObject>?, msg: String) -> Unit) {
        repository.queryBannerData(callback)
    }
    /**
     * 模糊查询能选的文章
     */
    fun searchAnyThesis(searchTitle:String){
        repository.searchAnyThesis(searchTitle){isSuccess, thesis, msg ->
            if (isSuccess){
                queryThesisListLiveData.value = thesis
            }else{
                Log.v(LOG_TAG,"searchAnyThesis：$isSuccess $thesis $msg")
            }
        }
    }
    /**
     * 添加Thesis测试方法
     */
    fun addThesis(thesis: Thesis,callback: (isSuccess:Boolean,objectId:String?,msg:String?) -> Unit){
        repository.addThesis(thesis,callback)
    }

//    /**
//     * 模糊查询能选的文章
//     */
//    fun getSearchAnyThesis(searchTitle:String):MutableLiveData<MutableList<Thesis>>{
//        val mutableListOf = mutableListOf<Thesis>()
//        BmobQuery<Thesis>()
//            .addWhereContains("title",searchTitle)
//            .findObjects(object : FindListener<Thesis>(){
//                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
//                    if(p1 == null && p0 != null){
//
//                    }else{
//
//                    }
//                }
//            })
//
//    }
}