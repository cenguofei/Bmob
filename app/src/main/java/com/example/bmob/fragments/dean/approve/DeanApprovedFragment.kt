package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentDeanApprovedBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.ApprovedNotApprovedViewModel
import com.example.bmob.viewmodels.SetViewModel

class DeanApprovedFragment : Fragment() {
    private lateinit var binding: FragmentDeanApprovedBinding
    private val viewModel: ApprovedNotApprovedViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeanApprovedBinding.inflate(inflater, container, false)

        setViewModel.getUserByQuery().value?.let {
            binding.dean = it
            viewModel.getQueryThesisToDeanApprovedLiveData(
                it,
                ALREADY_APPROVED
            ) { msg -> showMsg(requireContext(), msg) }
                .observe(viewLifecycleOwner) { data -> initAdapter(data) }
        }
        return binding.root
    }

    private fun initAdapter(thesisList: MutableList<MutableList<Thesis>>) {
        val approveThesisAdapter = ApproveThesisAdapter(thesisList) { thesis ->
            val actionDeanApprovedFragmentToApproveFragment =
                DeanApprovedFragmentDirections.actionDeanApprovedFragmentToApproveFragment(
                    thesis,
                    false
                )
            findNavController().navigate(actionDeanApprovedFragmentToApproveFragment)
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