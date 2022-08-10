package com.example.bmob.fragments.student

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.databinding.FragmentStudentHomeBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.askPermission
import com.youth.banner.indicator.CircleIndicator

/**
 * 学生首页
 *
 * 学生选择课题：
 */
class StudentHomeFragment : BaseFragment<SearchViewModel, FragmentStudentHomeBinding>() {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        askPermission()
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