package com.example.bmob.fragments.student

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.bmob.databinding.FragmentStudentHomeBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.CommonHomeViewModel
import com.example.bmob.viewmodels.ERROR
import com.example.bmob.viewmodels.SetViewModel
import com.youth.banner.indicator.CircleIndicator

/**
 * 学生首页
 *
 * 学生选择课题：
 *
 */
class StudentHomeFragment : Fragment(), FragmentEventListener {
    lateinit var binding: FragmentStudentHomeBinding
    private var adapter: SearchRecyclerViewAdapter? = null

    private val viewModel:CommonHomeViewModel by viewModels()
    //activityViewModels相当于单例模式，此处用setViewModel是保证用户修改数据后同步数据到改界面
    private val setViewModel:SetViewModel by activityViewModels()

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

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Log.v(LOG_TAG, "用户拒绝权限请求")
            }
        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        setEventListener()
        viewModel.setFragment(this)

        binding.banner.addBannerLifecycleObserver(this)

        setViewModel.getUserByQuery().observe(viewLifecycleOwner){
            binding.user = it
        }
        //观测搜索结果
        viewModel.searchResult.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "观测到数据：$it")
            if (it.first != ERROR) {
                if (adapter == null && it.second.isNotEmpty()) {
                    viewModel.isShowRecyclerView(binding.recyclerView,binding.contentLinearLayout,true)
                    adapter = SearchRecyclerViewAdapter(it.second) { thesis ->
                        Log.v(LOG_TAG, "回调：$thesis")
                        val actionStudentHomeFragmentToShowThesisFragment =
                            StudentHomeFragmentDirections.actionStudentHomeFragmentToShowThesisFragment(
                                thesis,false
                            )
                        findNavController().navigate(actionStudentHomeFragmentToShowThesisFragment)
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL, false
                    )
                    binding.recyclerView.adapter = adapter
                } else {
                    if (it.second.isNotEmpty()/** && viewModel.getNowSearch().value == it.first*/) {
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
            val actionStudentHomeFragmentToBrowseFragment =
                StudentHomeFragmentDirections.actionStudentHomeFragmentToBrowseFragment()
            findNavController().navigate(actionStudentHomeFragmentToBrowseFragment)
        }
        binding.myThesis1.setOnClickListener {
            //显示学生的已选的课题
            findNavController().navigate(R.id.action_studentHomeFragment_to_studentThesisFragment)
        }
        viewModel.setSearchViewListener(binding.searchView,binding.recyclerView,binding.contentLinearLayout){
            if (it){
                Log.v(LOG_TAG,"输入的内容空")
//                adapter?.data?.clear()
//                adapter?.data = null

                adapter = null
            }
        }
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