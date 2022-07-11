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
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.BmobBannerObject
import com.example.bmob.data.entity.ReleaseTime
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.utils.EMPTY_TEXT
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.School
import com.example.bmob.utils.showMsg
import java.text.SimpleDateFormat
import java.util.*

class CommonHomeViewModel(private val handler: SavedStateHandle) : ViewModel() {
    private val repository = BmobRepository.getInstance()
    private var nowSearch = MutableLiveData<String>()
    private lateinit var fragment: Fragment

    fun setFragment(fragment: Fragment) {
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
        val results: MutableLiveData<Pair<String, MutableList<Thesis>>> =
            MutableLiveData<Pair<String, MutableList<Thesis>>>()
        repository.searchAnyThesis(query) { isSuccess, thesisList, msg ->
            if (isSuccess) {
                results.value = thesisList
            } else {
                Log.v(LOG_TAG, "模糊查询能选的文章失败：$msg")
                results.value = Pair(ERROR, mutableListOf())
            }
        }
        return results
    }

    //初始化界面
    fun isShowRecyclerView(
        recyclerView: RecyclerView,
        linearLayout: LinearLayout,
        isShow: Boolean
    ) {
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
        callback: (isTextEmpty: Boolean) -> Unit
    ) {
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
                    isShowRecyclerView(recyclerView, linearLayout, false)
                    false
                }
            }
        })
    }

    /**
     * 搜索学生首页的banner
     */
    fun queryBannerData(): MutableLiveData<MutableList<BmobBannerObject>> {
        if (!handler.contains(BANNER_DATA)) {
            repository.queryBannerData { isSuccess, data, msg ->
                if (isSuccess) {
                    handler.set(BANNER_DATA, data)
                } else {
                    showMsg(fragment.requireContext(), msg)
                }
            }
        }
        return handler.getLiveData(BANNER_DATA)
    }

    /**
     * 针对学生用户
     *
     * 每次学生登录的时候都检查自己的选题状态
     *
     * 用当前时间和教务长发布的选题时间作比较，
     * 如果时间已过期，即选题时间段在当前时间段之前，
     * 就要把学生的选题状态改为false（如果本来就是false也可以不用改）
     */
    fun updateStudentSelectState(
        student: User,
        releaseTime: ReleaseTime,
        callback: (isSuccess: Boolean, student:User?,msg: String) -> Unit
    ) {
        student.studentSelectState = determineStudentSelectStateByReleaseTime(student,releaseTime)
        if (student.studentSelectState == false){
            student.studentThesis = null
        }
        student.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    callback.invoke(true,student, EMPTY_TEXT)
                } else {
                    callback.invoke(false,null, p0.message.toString())
                }
            }
        })
    }

    fun queryIssuedReleaseTime(student: User, callback: (release: ReleaseTime?) -> Unit) {
        BmobQuery<ReleaseTime>()
            .addWhereEqualTo(School, student.school)
            .findObjects(object : FindListener<ReleaseTime>() {
                override fun done(
                    p0: MutableList<ReleaseTime>?,
                    p1: BmobException?
                ) {
                    if (p1 == null && p0 != null && p0.isNotEmpty()) {
                        callback.invoke(p0[0])
                    }else callback.invoke(null)
                }
            })
    }

    /**
     * 通过releaseTime决定学生的studentSelectState属性是true还是false
     */
    private fun determineStudentSelectStateByReleaseTime(student:User,releaseTime: ReleaseTime): Boolean {
        return try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val endTime = simpleDateFormat.parse(releaseTime.endTime)

            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH) + 1
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val minute: Int = calendar.get(Calendar.MINUTE)
            val second: Int = calendar.get(Calendar.SECOND)

            val dateSystem = simpleDateFormat.parse("$year-$month-$day $hour:$minute:$second")

            if (dateSystem != null) {
                if (dateSystem.after(endTime)) {
                    Log.v(LOG_TAG,"系统时间大于 选题结束时间  更新为false")
                    return false
                }
            }
//            Log.v(LOG_TAG,"系统时间小于 选题结束时间  更新为true")

//            return student.studentSelectState != false
            if (student.studentSelectState == false){
                Log.v(LOG_TAG,"更新为false")
                return false
            }else return true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private const val BANNER_DATA = "_banner_data_"
    }
}

