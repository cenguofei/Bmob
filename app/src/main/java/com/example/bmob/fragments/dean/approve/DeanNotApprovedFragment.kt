package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.data.entity.NOT_APPROVED
import com.example.bmob.databinding.FragmentDeanApprovedBinding
import com.example.bmob.databinding.FragmentDeanNotApprovedBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.ApprovedNotApprovedViewModel
import com.example.bmob.viewmodels.SetViewModel

class DeanNotApprovedFragment : Fragment() {
    private lateinit var binding: FragmentDeanNotApprovedBinding
    private val viewModel: ApprovedNotApprovedViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeanNotApprovedBinding.inflate(inflater, container, false)

        binding.dean = setViewModel.getUserByQuery().value
        viewModel.getQueryThesisToDeanNotApprovedLiveData(
            setViewModel.getUserByQuery().value!!,
            NOT_APPROVED
        ){
            showMsg(requireContext(),it)
        }.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val approveThesisAdapter = ApproveThesisAdapter(it!!){thesis->
                    val actionDeanNotApprovedFragmentToApproveFragment =
                        DeanNotApprovedFragmentDirections.actionDeanNotApprovedFragmentToApproveFragment(
                            thesis,
                            true
                        )
                    findNavController().navigate(actionDeanNotApprovedFragmentToApproveFragment)
                    Log.v(LOG_TAG,"approveThesisAdapter 被点击了：$thesis")
                }
                binding.recyclerView.run {
                    layoutManager = LinearLayoutManager(requireContext(),
                        RecyclerView.VERTICAL,false)
                    adapter = approveThesisAdapter
                }
            }
        }
        return binding.root
    }
}