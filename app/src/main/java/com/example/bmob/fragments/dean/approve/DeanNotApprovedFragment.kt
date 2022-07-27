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
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentDeanNotApprovedBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.DeanApproveViewModel
import com.example.bmob.viewmodels.SetViewModel

class DeanNotApprovedFragment : Fragment() {
    private lateinit var binding: FragmentDeanNotApprovedBinding
    private val viewModel: DeanApproveViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeanNotApprovedBinding.inflate(inflater, container, false)

        setViewModel.getUserByQuery().value?.let {
            binding.dean = it
            viewModel.getQueryThesisToDeanNotApprovedLiveData(
                it,
                NOT_APPROVED
            ) { msg ->  showMsg(requireContext(), msg) }
                .observe(viewLifecycleOwner) { data -> initAdapter(data) }
        }
        return binding.root
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