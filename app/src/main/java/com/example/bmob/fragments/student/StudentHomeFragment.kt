package com.example.bmob.fragments.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentStudentHomeBinding
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmob.viewmodels.SetViewModel
import com.youth.banner.indicator.CircleIndicator

/**
 * 学生首页
 *
 * 学生选择课题：
 */
class StudentHomeFragment : Fragment(), FragmentEventListener {
    lateinit var binding: FragmentStudentHomeBinding
    private val viewModel: SearchViewModel by viewModels()

    //activityViewModels相当于单例模式，此处用setViewModel是保证用户修改数据后同步数据到改界面
    private val setViewModel: SetViewModel by activityViewModels()

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

        setViewModel.getUserByQuery().observe(viewLifecycleOwner) {
            binding.user = it
        }

        //观测banner数据
        viewModel.queryBannerData().observe(viewLifecycleOwner) {
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
        binding.searchViewTv.setOnClickListener {
            val actionStudentHomeFragmentToSearchFragment =
                StudentHomeFragmentDirections.actionStudentHomeFragmentToSearchFragment(true)
            findNavController().navigate(actionStudentHomeFragmentToSearchFragment)
        }

        binding.graduateThesis.setOnClickListener {
            val actionStudentHomeFragmentToBrowseFragment =
                StudentHomeFragmentDirections.actionStudentHomeFragmentToBrowseFragment()
            findNavController().navigate(actionStudentHomeFragmentToBrowseFragment)
        }
        binding.myThesis1.setOnClickListener {
            //显示学生的已选的课题
            findNavController().navigate(R.id.action_studentHomeFragment_to_studentThesisFragment)
        }
    }
}