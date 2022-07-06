package com.example.bmob.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.bmob.data.entity.BmobBannerObject
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.repository.remote.BmobRepository

class StudentHomeViewModel : ViewModel() {
    private val repository = BmobRepository.getInstance()


    private var nowSearch = MutableLiveData<String>()

    fun setNowSearch(query: String) {
        nowSearch.postValue(query)
    }

    var searchResult = Transformations.switchMap(nowSearch) { query ->
        switchSearchAnyThesis(query)
    }

    /**
     * 模糊查询能选的文章
     */
    private fun switchSearchAnyThesis(query: String): MutableLiveData<Pair<String,MutableList<Thesis>>> {
        val results: MutableLiveData<Pair<String,MutableList<Thesis>>> = MutableLiveData<Pair<String,MutableList<Thesis>>>()
        repository.searchAnyThesis(query) { isSuccess, thesisList, msg ->
            if (isSuccess) {
                results.value = thesisList
            } else {
                results.value = Pair(ERROR, mutableListOf())
            }
        }
        return results
    }

    /**
     * 搜索学生首页的banner
     */
    fun queryBannerData(callback: (isSuccess: Boolean, data: MutableList<BmobBannerObject>?, msg: String) -> Unit) {
        repository.queryBannerData(callback)
    }


    /**
     * 添加Thesis测试方法
     */
    fun addThesis(
        thesis: Thesis,
        callback: (isSuccess: Boolean, objectId: String?, msg: String?) -> Unit
    ) {
        repository.addThesis(thesis, callback)
    }
}

const val ERROR = "_error_"




/**
 * 模糊查询能选的文章
 *
 * 用这种方法直接查找到的课题不能及时显示到搜索结果的显示页面中，
 * 需要用switchMap转换
 */
//    fun searchAnyThesis(searchTitle: String) {
//        viewModelScope.launch {
//            repository.searchAnyThesis(searchTitle) { isSuccess, thesisList, msg ->
//                if (isSuccess) {
//                    queryThesisListLiveData.value = thesisList
//                } else {
//                    Log.v(LOG_TAG, "searchAnyThesis：$isSuccess $thesisList $msg")
//                }
//            }
//        }
//    }
