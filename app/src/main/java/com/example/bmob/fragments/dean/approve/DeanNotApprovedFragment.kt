package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.ApproveThesisAdapter
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentDeanNotApprovedBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.viewmodels.DeanApproveViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort

class DeanNotApprovedFragment :
    BaseFragment<DeanApproveViewModel, FragmentDeanNotApprovedBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        binding.dean = appUser
        viewModel.getQueryThesisToDeanNotApprovedLiveData(
            appUser,
            NOT_APPROVED
        ) { msg -> showMsgShort(msg) }
            .observe(viewLifecycleOwner) { data -> initAdapter(data) }
    }

    private fun initAdapter(thesisList: MutableList<MutableList<Thesis>>) {
        val approveThesisAdapter = ApproveThesisAdapter(thesisList) { thesis ->
            val actionDeanNotApprovedFragmentToApproveFragment =
                DeanNotApprovedFragmentDirections.actionDeanNotApprovedFragmentToApproveFragment(
                    thesis,
                    true
                )
            findNavController().navigate(actionDeanNotApprovedFragmentToApproveFragment)
        }
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL, false
            )
            adapter = approveThesisAdapter
        }
    }
}