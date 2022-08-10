package com.example.bmob.fragments.dean

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.databinding.FragmentDeanHomeBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.askPermission
import com.youth.banner.indicator.CircleIndicator

class DeanHomeFragment : BaseFragment<SearchViewModel, FragmentDeanHomeBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        binding.click = ProxyClick()
    }

    override fun createObserver() {
        appViewModel.user.observe(viewLifecycleOwner) {
            binding.user = it
        }

        //观测banner数据
        viewModel.queryBannerData().observe(viewLifecycleOwner) {
            binding.banner
                .setAdapter(BannerAdapter(it!!))
                .indicator = CircleIndicator(requireContext())
        }

        binding.banner.addBannerLifecycleObserver(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        askPermission()
    }

    override fun onStart() {
        super.onStart()
        binding.banner.start()
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

    inner class ProxyClick {
        fun onSearch() {
            val actionDeanHomeFragmentToSearchFragment =
                DeanHomeFragmentDirections.actionDeanHomeFragmentToSearchFragment(false)
            findNavController().navigate(actionDeanHomeFragmentToSearchFragment)
        }

        //已审批课题
        fun onApproveThesis() {
            findNavController().navigate(R.id.action_deanHomeFragment_to_deanApprovedFragment)
        }

        //未审批课题
        fun onNotApproveThesisLayout() {
            findNavController().navigate(R.id.action_deanHomeFragment_to_deanNotApprovedFragment)
        }

        //已选学生名单  选题结果
        fun onSelectedResultLayout() {
            findNavController().navigate(R.id.action_deanHomeFragment_to_studentSelectedFragment)
        }

        //未选学生名单
        fun onUnselectedStudents() {
            findNavController().navigate(R.id.action_deanHomeFragment_to_studentUnselectedFragment)
        }
    }
}