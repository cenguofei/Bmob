package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.THESIS_APPROVE_REJECTED
import com.example.bmob.databinding.FragmentApproveBinding
import com.example.bmob.viewmodels.DeanApproveViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort

class ApproveFragment : BaseFragment<DeanApproveViewModel, FragmentApproveBinding>() {
    private val args: ApproveFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        binding.thesis = args.deanApproveThesis
        binding.click = ProxyClick()
        if (!args.isShowActionButton) {
            binding.repulseBtn.visibility = View.GONE
            binding.agreeBtn.visibility = View.GONE
        }
    }

    inner class ProxyClick {
        //拒绝
        fun onRepulse() {
            viewModel.updateThesisForDeanApprove(
                args.deanApproveThesis,
                THESIS_APPROVE_REJECTED
            ) { _, message ->
                showMsgShort(message)
            }
        }

        fun onBack() {
            findNavController().navigateUp()
        }

        //同意课题申请
        fun onAgree() {
            viewModel.updateThesisForDeanApprove(
                args.deanApproveThesis,
                ALREADY_APPROVED
            ) { _, message ->
                showMsgShort(message)
            }
        }
    }
}