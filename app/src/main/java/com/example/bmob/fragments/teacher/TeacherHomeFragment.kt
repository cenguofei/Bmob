package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.SearchRecyclerViewAdapter
import com.example.bmob.databinding.FragmentTeacherHomeBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.CommonHomeViewModel
import com.example.bmob.viewmodels.CommonHomeViewModel.Companion.ERROR
import com.example.bmob.viewmodels.SetViewModel
import com.youth.banner.indicator.CircleIndicator

class TeacherHomeFragment : Fragment(),FragmentEventListener {
    lateinit var binding:FragmentTeacherHomeBinding
    private var adapter: SearchRecyclerViewAdapter? = null
    private val viewModel: CommonHomeViewModel by viewModels()
    //activityViewModels相当于单例模式，此处用setViewModel是保证用户修改数据后同步数据到改界面
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.banner1.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListener()
        viewModel.setFragment(this)

        binding.banner1.addBannerLifecycleObserver(this)

        setViewModel.getUserByQuery().observe(viewLifecycleOwner){
            binding.user = it
        }
        //观测搜索结果
        viewModel.searchResult.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "观测到数据：$it")
            if (it.first != ERROR) {
                if (adapter == null && it.second.isNotEmpty()) {
                    viewModel.isShowRecyclerView(binding.recyclerView1,binding.contentLinearLayout1,true)
                    adapter = SearchRecyclerViewAdapter(it.second) { thesis ->
                        Log.v(LOG_TAG, "回调：$thesis")
                        val actionTeacherHomeFragmentToShowThesisFragment =
                            TeacherHomeFragmentDirections.actionTeacherHomeFragmentToShowThesisFragment(
                                thesis,false
                            )
                        findNavController().navigate(actionTeacherHomeFragmentToShowThesisFragment)
                    }
                    binding.recyclerView1.adapter = adapter
                    binding.recyclerView1.layoutManager = LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL, false
                    )
                } else {
                    if (it.second.isNotEmpty() && viewModel.getNowSearch().value == it.first) {
                        Log.v(LOG_TAG, "设置thesisList：$it")
                        viewModel.isShowRecyclerView(binding.recyclerView1,binding.contentLinearLayout1,true)
                        adapter!!.setThesisList(it.second)
                    } else {
                        viewModel.isShowRecyclerView(binding.recyclerView1,binding.contentLinearLayout1,false)
                    }
                }
            }
        }
        //观测banner数据
        viewModel.queryBannerData().observe(viewLifecycleOwner){
            binding.banner1
                .setAdapter(BannerAdapter(it!!))
                .indicator = CircleIndicator(requireContext())
        }
    }


    override fun onStop() {
        super.onStop()
        binding.banner1.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.banner1.destroy()
    }

    override fun setEventListener() {
        //导航到 我的课题 详情页面，查看已经 上传 的课题
        binding.selectedResultLayout.setOnClickListener {
            findNavController().navigate(R.id.action_teacherHomeFragment_to_teacherReleasedFragment)
        }
        //查看选择自己每个课题的学生
        binding.selectedStudentListLinearLayout.setOnClickListener {
            findNavController().navigate(R.id.action_teacherHomeFragment_to_teacherSelectResultFragment)
        }
        viewModel.setSearchViewListener(binding.searchView1,binding.recyclerView1,binding.contentLinearLayout1){
            if (it){
                Log.v(LOG_TAG,"输入的内容空")
                adapter = null
            }
        }
    }
}