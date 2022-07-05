package com.example.bmob.fragments.student

import android.os.Bundle
import android.text.TextUtils
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
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.databinding.FragmentStudentHomeBinding
import com.example.bmob.fragments.thesis.ShowThesisFragmentArgs
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmob.viewmodels.StudentHomeViewModel
import com.example.bmob.viewmodels.StudentSelectViewModel
import com.youth.banner.indicator.CircleIndicator

/**
 * 学生首页
 */
class StudentHomeFragment : Fragment() ,FragmentEventListener{
    private lateinit var binding: FragmentStudentHomeBinding
    private val model: StudentHomeViewModel by activityViewModels()
    private var adapter: SearchRecyclerViewAdapter? = null
    private val userViewModel: BmobUserViewModel by activityViewModels()

    private val testViewModel:StudentSelectViewModel by viewModels()

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

        binding.banner.addBannerLifecycleObserver(this)
        model.queryThesisListLiveData.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "观测到数据：$it")
            if (adapter == null) {
                adapter = SearchRecyclerViewAdapter(it) { thesis ->
                    Log.v(LOG_TAG, "回调：$thesis")
                    val actionStudentHomeFragmentToShowThesisFragment =
                        StudentHomeFragmentDirections.actionStudentHomeFragmentToShowThesisFragment(
                            thesis
                        )
                    findNavController().navigate(actionStudentHomeFragmentToShowThesisFragment)
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
    override fun setEventListener() {
        binding.graduateThesis.setOnClickListener {
            findNavController().navigate(R.id.action_studentHomeFragment_to_selectFragment)
        }

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