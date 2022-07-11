package com.example.bmob.viewmodels

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.SearchRecyclerViewAdapter
import com.example.bmob.data.entity.BmobBannerObject
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.fragments.student.StudentHomeFragment
import com.example.bmob.fragments.teacher.TeacherHomeFragment
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg

class CommonHomeViewModel(private val handler:SavedStateHandle):ViewModel() {
    private val repository = BmobRepository.getInstance()
    private var nowSearch = MutableLiveData<String>()
    private lateinit var fragment:Fragment

    fun setFragment(fragment: Fragment){
        this.fragment = fragment
    }

    //设置当前的搜索内容
    fun setNowSearch(query: String) {
        nowSearch.postValue(query)
    }
    fun getNowSearch() = nowSearch

    //外部观察
    var searchResult = Transformations.switchMap(nowSearch) { query ->
        switchSearchAnyThesis(query)
    }

    /**
     * 模糊查询能选的文章
     */
    private fun switchSearchAnyThesis(query: String): MutableLiveData<Pair<String, MutableList<Thesis>>> {
        val results: MutableLiveData<Pair<String, MutableList<Thesis>>> = MutableLiveData<Pair<String,MutableList<Thesis>>>()
        repository.searchAnyThesis(query) { isSuccess, thesisList, msg ->
            if (isSuccess) {
                results.value = thesisList
            } else {
                Log.v(LOG_TAG,"模糊查询能选的文章失败：$msg")
                results.value = Pair(ERROR, mutableListOf())
            }
        }
        return results
    }

    //初始化界面
    fun isShowRecyclerView(recyclerView: RecyclerView,linearLayout: LinearLayout,isShow: Boolean) {
        if (isShow) {
            recyclerView.visibility = View.VISIBLE
            linearLayout.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            linearLayout.visibility = View.VISIBLE
        }
    }

    fun setSearchViewListener(
        searchView: SearchView,
        recyclerView: RecyclerView,
        linearLayout: LinearLayout,
        callback:(isTextEmpty:Boolean)->Unit
    ){
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            //点击搜索时调用
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            //此处可以设置按输入给出提示的adapter
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.v(LOG_TAG, "newText:${newText}")
                return if (!TextUtils.isEmpty(newText)) {
                    setNowSearch(newText!!)
                    true
                } else {
                    callback.invoke(true)
                    isShowRecyclerView(recyclerView,linearLayout,false)
                    false
                }
            }
        })
    }

    /**
     * 搜索学生首页的banner
     */
    fun queryBannerData():MutableLiveData<MutableList<BmobBannerObject>> {
        if (!handler.contains(BANNER_DATA)){
            repository.queryBannerData{isSuccess, data, msg ->
                if (isSuccess) {
                    handler.set(BANNER_DATA,data)
                } else {
                    showMsg(fragment.requireContext(), msg)
                }
            }
        }
        return handler.getLiveData(BANNER_DATA)
    }

    companion object{
        private const val BANNER_DATA = "_banner_data_"
    }
}

