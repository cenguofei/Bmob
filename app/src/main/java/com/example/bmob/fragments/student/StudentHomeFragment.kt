package com.example.bmob.fragments.student

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.databinding.FragmentStudentHomeBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmob.viewmodels.StudentHomeViewModel
import com.youth.banner.indicator.CircleIndicator

/**
 * 学生首页
 */
class StudentHomeFragment : Fragment() {
    private lateinit var binding: FragmentStudentHomeBinding
    private val model: StudentHomeViewModel by activityViewModels()
    private var adapter: SearchRecyclerViewAdapter? = null
    private val userViewModel: BmobUserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        userViewModel.getUserInfo { isSuccess, user ->
            if (isSuccess) {
                binding.avatarUrl = user!!.avatarUrl
            } else {
                binding.headImg.setImageResource(R.drawable.ic_launcher_background)
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.banner.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
        binding.banner.addBannerLifecycleObserver(this)

        model.queryThesisListLiveData.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "观测到数据：$it")
            if (adapter == null) {
                adapter = SearchRecyclerViewAdapter(it) { thesis ->
                    Log.v(LOG_TAG, "回调：$thesis")
                }
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL, false
                )
            }
            Log.v(LOG_TAG, "设置thesisList：$it")
            adapter!!.setThesisList(it)
            isShowRecyclerView(true)
        }

        //测试
        binding.textView4.setOnClickListener {
            model.addThesis(
                Thesis(
                    listOf("1", "2"), 1, "title",
                    "内容", "描述", "方向为Android", "教师id", "教师名字",
                    listOf("学生1 id", "学生2 id"), 2, false, 1, 99, "西南大学",
                    "计算机系", "商贸学院"
                )
            ) { isSuccess, objectId, msg ->
                if (isSuccess) {
                    Log.v(LOG_TAG, "论文添加成功,id为：$objectId")
                } else {
                    Log.v(LOG_TAG, "论文添加失败：$msg")
                }
            }
            BmobRepository.getInstance().searchAnyThesis("title") { isSuccess, thesis, msg ->
                Log.v(
                    LOG_TAG,
                    "size:${thesis?.size},isSuccess:$isSuccess thesis:$thesis msg:$msg\n"
                )
            }
        }

        model.queryBannerData { isSuccess, data, msg ->
            if (isSuccess) {
                binding.banner
                    .setAdapter(StudentBannerAdapter(data!!))
                    .indicator = CircleIndicator(requireContext())
            } else {
                showMsg(requireContext(), msg)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.banner.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.banner.destroy()
    }

    //初始化界面
    private fun isShowRecyclerView(isShow: Boolean) {
        if (isShow) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.contentLinearLayout.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.contentLinearLayout.visibility = View.VISIBLE
        }
    }

    //设置点击事件
    private fun setEventListener() {
        binding.myClass.setOnClickListener {
            findNavController().navigate(R.id.action_studentHomeFragment_to_browseFragment)
        }

        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            //点击搜索时调用
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //此处可以设置按输入给出提示的adapter
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.v(LOG_TAG, "newText:${newText}")
                if (!TextUtils.isEmpty(newText)) {
                    model.searchAnyThesis(newText!!)
                } else {
                    isShowRecyclerView(false)
                }
                return true
            }
        })
    }
}