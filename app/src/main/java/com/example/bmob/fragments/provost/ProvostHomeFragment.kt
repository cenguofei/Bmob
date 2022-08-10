package com.example.bmob.fragments.provost

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.FragmentProvostHomeBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.askPermission
import com.youth.banner.indicator.CircleIndicator


class ProvostHomeFragment : BaseFragment<SearchViewModel, FragmentProvostHomeBinding>() {

    override fun onStart() {
        super.onStart()
        binding.banner.start()
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setFragment(this)
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
            //通过的权限
            val grantedList = it.filterValues { it }.mapNotNull { it.key }
            //是否所有权限都通过
            val allGranted = grantedList.size == it.size
            val list = (it - grantedList.toSet()).map { it.key }
            //未通过的权限
            val deniedList = list.filter {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    it
                )
            }
            //拒绝并且点了“不再询问”权限
            val alwaysDeniedList = list - deniedList.toSet()
        }.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
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

    inner class ProxyClick {
        fun onSearchViewTv() {
            val actionProvostHomeFragmentToSearchFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSearchFragment(false)
            findNavController().navigate(actionProvostHomeFragmentToSearchFragment)
        }

        fun onIssueTime() {
            findNavController().navigate(R.id.action_provostHomeFragment_to_provostSelectTimeFragment)
        }

        //查看老师信息
        fun onTeacherInfo() {
            val actionProvostHomeFragmentToSkimFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSkimFragment(
                    IDENTIFICATION_TEACHER
                )
            findNavController().navigate(actionProvostHomeFragmentToSkimFragment)
        }

        //查看学生信息
        fun onStudentInfo() {
            val actionProvostHomeFragmentToSkimFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSkimFragment(
                    IDENTIFICATION_STUDENT
                )
            findNavController().navigate(actionProvostHomeFragmentToSkimFragment)
        }

        //查看系主任信息
        fun onDeanInfo() {
            val actionProvostHomeFragmentToSkimFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSkimFragment(
                    IDENTIFICATION_DEAN
                )
            findNavController().navigate(actionProvostHomeFragmentToSkimFragment)
        }
    }
}