package com.example.bmob.fragments.dean

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
import com.example.bmob.databinding.FragmentDeanHomeBinding
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmob.viewmodels.SetViewModel
import com.youth.banner.indicator.CircleIndicator

class DeanHomeFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentDeanHomeBinding
    private val viewModel: SearchViewModel by viewModels()

    //activityViewModels相当于单例模式，此处用setViewModel是保证用户修改数据后同步数据到改界面
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeanHomeBinding.inflate(inflater, container, false)
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

    override fun setEventListener() {
        binding.searchViewTv.setOnClickListener {
            val actionDeanHomeFragmentToSearchFragment =
                DeanHomeFragmentDirections.actionDeanHomeFragmentToSearchFragment(false)
            findNavController().navigate(actionDeanHomeFragmentToSearchFragment)
        }

        //已审批课题
        binding.approveThesisLayout.setOnClickListener {
            findNavController().navigate(R.id.action_deanHomeFragment_to_deanApprovedFragment)
        }
        //未审批课题
        binding.notApproveThesisLayout.setOnClickListener {
            findNavController().navigate(R.id.action_deanHomeFragment_to_deanNotApprovedFragment)
        }
        //已选学生名单  选题结果
        binding.selectedResultLayout.setOnClickListener {
            findNavController().navigate(R.id.action_deanHomeFragment_to_studentSelectedFragment)
        }

        //未选学生名单
        binding.unselectedStudents.setOnClickListener {
            findNavController().navigate(R.id.action_deanHomeFragment_to_studentUnselectedFragment)
        }
    }
}