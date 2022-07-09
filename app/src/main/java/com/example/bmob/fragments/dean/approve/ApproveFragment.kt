package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.data.entity.THESIS_APPROVED_REJECTED
import com.example.bmob.databinding.FragmentApproveBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.DeanApproveViewModel

/**
 * 点击某个课题进行审批的页面
 */
class ApproveFragment : Fragment() ,FragmentEventListener{
    private lateinit var binding:FragmentApproveBinding
    private val args:ApproveFragmentArgs by navArgs()
    private val viewModel:DeanApproveViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApproveBinding.inflate(inflater,container,false)
        binding.thesis = args.deanApproveThesis
        if (!args.isShowActionButton){
            binding.repulseBtn.visibility = View.GONE
            binding.agreeBtn.visibility = View.GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        //拒绝
        binding.repulseBtn.setOnClickListener {
            viewModel.updateThesisForDeanApprove(args.deanApproveThesis, THESIS_APPROVED_REJECTED){ _, message->
                showMsg(requireContext(),message)
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        //同意课题申请
        binding.agreeBtn.setOnClickListener {
            viewModel.updateThesisForDeanApprove(args.deanApproveThesis, ALREADY_APPROVED){isSuccess,message->
                if (isSuccess){
                    binding.repulseBtn.visibility = View.GONE
                }
                showMsg(requireContext(),message)
            }
        }

    }
}