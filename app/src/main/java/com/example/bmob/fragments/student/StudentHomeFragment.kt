package com.example.bmob.fragments.student

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.SearchRecyclerViewAdapter
import com.example.bmob.databinding.FragmentStudentHomeBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.CommonViewModel
import com.example.bmob.viewmodels.EMPTY_SEARCH
import com.example.bmob.viewmodels.ERROR
import com.youth.banner.indicator.CircleIndicator

/**
 * 学生首页
 */
class StudentHomeFragment : Fragment(), FragmentEventListener {
    lateinit var binding: FragmentStudentHomeBinding
    private var adapter: SearchRecyclerViewAdapter? = null
    private val viewModel:CommonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.banner.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
        viewModel.setFragment(this)

        binding.banner.addBannerLifecycleObserver(this)
        //观测头像url并保存到handler
        viewModel.getStudentInfo(this).observe(viewLifecycleOwner){
            if (it == EMPTY_SEARCH){
                binding.headImg.setImageResource(R.drawable.default_head)
            }else{
                binding.avatarUrl = it
            }
        }
        //观测搜索结果
        viewModel.searchResult.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "观测到数据：$it")
            if (it.first != ERROR) {
                if (adapter == null && it.second.isNotEmpty()) {
                    adapter = SearchRecyclerViewAdapter { thesis ->
                        Log.v(LOG_TAG, "回调：$thesis")
                        val actionStudentHomeFragmentToShowThesisFragment =
                            StudentHomeFragmentDirections.actionStudentHomeFragmentToShowThesisFragment(
                                thesis
                            )
                        findNavController().navigate(actionStudentHomeFragmentToShowThesisFragment)
                    }
                    adapter!!.setThesisListForFirst(it.second)
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL, false
                    )
                } else {
                    if (it.second.isNotEmpty() && viewModel.nowSearch.value == it.first) {
                        Log.v(LOG_TAG, "设置thesisList：$it")
                        viewModel.isShowRecyclerView(binding.recyclerView,binding.contentLinearLayout,true)
                        adapter!!.setThesisList(it.second)
                    } else {
                        viewModel.isShowRecyclerView(binding.recyclerView,binding.contentLinearLayout,false)
                    }
                }
            }
        }
        //观测banner数据
        viewModel.queryBannerData().observe(viewLifecycleOwner){
            binding.banner
                .setAdapter(BannerAdapter(it!!))
                .indicator = CircleIndicator(requireContext())
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

    //设置点击事件
    override fun setEventListener() {
        binding.graduateThesis.setOnClickListener {
            findNavController().navigate(R.id.action_studentHomeFragment_to_selectFragment)
        }
        binding.myClass.setOnClickListener {
            findNavController().navigate(R.id.action_studentHomeFragment_to_browseFragment)
        }
        viewModel.setSearchViewListener(binding.searchView,binding.recyclerView,binding.contentLinearLayout)
    }
}


//测试
//        binding.headImg.setOnClickListener {
//            testViewModel.addStudentToThesis("", emptyList()){isSuccess, msg ->
//                if (isSuccess){
//                    Log.v(LOG_TAG,"成功添加学生到课题")
//                }else{
//                    Log.v(LOG_TAG,msg)
//                }
//            }
//        }