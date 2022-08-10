package com.example.bmob.fragments.teacher

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.databinding.FragmentTeacherHomeBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.askPermission
import com.youth.banner.indicator.CircleIndicator

class TeacherHomeFragment : BaseFragment<SearchViewModel, FragmentTeacherHomeBinding>() {

    override fun onStart() {
        super.onStart()
        binding.banner.start()
    }

    override fun createObserver() {
        binding.banner.addBannerLifecycleObserver(this)

        appViewModel.user.observe(viewLifecycleOwner) {
            binding.user = it
        }
        //观测banner数据
        viewModel.queryBannerData().observe(viewLifecycleOwner) {
            binding.banner
                .setAdapter(BannerAdapter(it!!))
                .indicator = CircleIndicator(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setFragment(this)
    }


    override fun onStop() {
        super.onStop()
        binding.banner.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.banner.destroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        askPermission()
    }

    override fun setEventListener() {
        binding.searchViewTv.setOnClickListener {
            val actionTeacherHomeFragmentToSearchFragment =
                TeacherHomeFragmentDirections.actionTeacherHomeFragmentToSearchFragment(false)
            findNavController().navigate(actionTeacherHomeFragmentToSearchFragment)
        }
        //导航到 我的课题 详情页面，查看已经 上传 的课题
        binding.selectedResultLayout.setOnClickListener {
            findNavController().navigate(R.id.action_teacherHomeFragment_to_teacherReleasedFragment)
        }
        //查看选择自己每个课题的学生
        binding.selectedStudentListLinearLayout.setOnClickListener {
            findNavController().navigate(R.id.action_teacherHomeFragment_to_teacherSelectResultFragment)
        }
    }
}