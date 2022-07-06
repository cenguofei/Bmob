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
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg

class CommonViewModel(private val handler:SavedStateHandle):ViewModel() {
    private val repository = BmobRepository.getInstance()
    var nowSearch = MutableLiveData<String>()
    private var adapter: SearchRecyclerViewAdapter? = null
    private lateinit var fragment:Fragment

    fun setFragment(fragment: Fragment){
        this.fragment = fragment
    }

    //设置当前的搜索内容
    private fun setNowSearch(query: String) {
        nowSearch.postValue(query)
    }

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

    /**
     * fragment通过observe观察数据
     */
    fun getStudentInfo(fragment:StudentHomeFragment):MutableLiveData<User>{
        if (!handler.contains(USER)){
            repository.getUserInfo{isSuccess, user ->
                if (isSuccess) {
                    handler.set(USER,user)
                } else {
                    handler.set(USER, EMPTY_SEARCH)
                    fragment.binding.headImg.setImageResource(R.drawable.default_head)
                }
            }
        }
        return handler.getLiveData(USER)
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

    fun setSearchViewListener(searchView: SearchView,recyclerView: RecyclerView,linearLayout: LinearLayout){
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            //点击搜索时调用
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //此处可以设置按输入给出提示的adapter
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.v(LOG_TAG, "newText:${newText}")
                if (!TextUtils.isEmpty(newText)) {
                    setNowSearch(newText!!)
                } else {
                    isShowRecyclerView(recyclerView,linearLayout,false)
                }
                return true
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

    fun setAdapter(
        directions:NavDirections,
        recyclerView: RecyclerView,
        data:Pair<String,MutableList<Thesis>>,
        linearLayout: LinearLayout
    ){
        Log.v(LOG_TAG, "观测到数据：$data")
        if (data.first != ERROR) {
            if (adapter == null && data.second.isNotEmpty()) {
                adapter = SearchRecyclerViewAdapter { thesis ->
                    Log.v(LOG_TAG, "回调：$thesis")
                    fragment.findNavController().navigate(directions)
                }
                adapter!!.setThesisListForFirst(data.second)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(
                    fragment.requireContext(),
                    RecyclerView.VERTICAL, false
                )
            } else {
                if (data.second.isNotEmpty() && nowSearch.value == data.first) {
                    Log.v(LOG_TAG, "设置thesisList：$data")
                    isShowRecyclerView(recyclerView,linearLayout,true)
                    adapter!!.setThesisList(data.second)
                } else {
                    isShowRecyclerView(recyclerView,linearLayout,false)
                }
            }
        }
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

private const val USER = "user_"
private const val BANNER_DATA = "banner_data_"

const val EMPTY_SEARCH = ""