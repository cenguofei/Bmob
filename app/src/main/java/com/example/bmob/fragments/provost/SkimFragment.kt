package com.example.bmob.fragments.provost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.FragmentSkimBinding
import com.example.bmob.databinding.ProvostSkimItemBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.ProvostSkimViewModel
import com.example.bmob.viewmodels.SetViewModel


class SkimFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentSkimBinding
    private val args:SkimFragmentArgs by navArgs()
    private val viewModel:ProvostSkimViewModel by activityViewModels()
    private val setViewModel:SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSkimBinding.inflate(inflater,container,false)
        showUserInfoByIdentification()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showUserInfoByIdentification(){
        when(args.identification){
            IDENTIFICATION_STUDENT -> {
                binding.identificationTv.text = "学生信息"
                viewModel.getStudentInfoLiveData(setViewModel.getUserByQuery().value!!){
                    showMsg(requireContext(),it)
                }.observe(viewLifecycleOwner){
                    RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent->
                        val itemInflater = LayoutInflater.from(parent.context)
                        RecyclerViewAdapter.ResultViewHolder(ProvostSkimItemBinding.inflate(itemInflater,parent,false))
                    }
                    val adapter = RecyclerViewAdapter(it){binding, result ->
                        (binding as ProvostSkimItemBinding).user = result
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),
                        RecyclerView.VERTICAL,false)
                    binding.recyclerView.adapter = adapter
                }
            }
            IDENTIFICATION_TEACHER -> {
                binding.identificationTv.text = "教师信息"
                viewModel.getTeacherInfoLiveData(setViewModel.getUserByQuery().value!!){
                    showMsg(requireContext(),it)
                }.observe(viewLifecycleOwner){
                    RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent->
                        val itemInflater = LayoutInflater.from(parent.context)
                        RecyclerViewAdapter.ResultViewHolder(ProvostSkimItemBinding.inflate(itemInflater,parent,false))
                    }
                    val adapter = RecyclerViewAdapter(it){binding, result ->
                        (binding as ProvostSkimItemBinding).user = result
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),
                        RecyclerView.VERTICAL,false)
                    binding.recyclerView.adapter = adapter
                }
            }
            IDENTIFICATION_DEAN -> {
                binding.identificationTv.text = "系主任信息"
                viewModel.getDeanInfoLiveData(setViewModel.getUserByQuery().value!!){
                    showMsg(requireContext(),it)
                }.observe(viewLifecycleOwner){
                    RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent->
                        val itemInflater = LayoutInflater.from(parent.context)
                        RecyclerViewAdapter.ResultViewHolder(ProvostSkimItemBinding.inflate(itemInflater,parent,false))
                    }
                    val adapter = RecyclerViewAdapter(it){binding, result ->
                        (binding as ProvostSkimItemBinding).user = result
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),
                        RecyclerView.VERTICAL,false)
                    binding.recyclerView.adapter = adapter
                }
            }
        }
    }
}