package com.example.bmob.fragments.teacher

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
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.databinding.FragmentTeacherSelectResultBinding
import com.example.bmob.databinding.StudentChooseThesisItemBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.TeacherSelectResultViewModel


class TeacherSelectResultFragment : Fragment(),FragmentEventListener{
    private lateinit var binding:FragmentTeacherSelectResultBinding
    private val viewModel:TeacherSelectResultViewModel by viewModels()
    //一定要用全局的SetViewModel，不然会空指针
    private val setViewModel:SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherSelectResultBinding.inflate(inflater,container,false)
        viewModel.getStudentOfSelectedThisThesisLiveData(setViewModel.getUserByQuery().value!!){
            showMsg(requireContext(),it)
        }.observe(viewLifecycleOwner){
            RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
                val itemInflater = LayoutInflater.from(parent.context)
                RecyclerViewAdapter.ResultViewHolder(
                    StudentChooseThesisItemBinding.inflate(
                        itemInflater,
                        parent,
                        false
                    )
                )
            }
            val adapter = RecyclerViewAdapter(it) { binding, result ->
                (binding as StudentChooseThesisItemBinding).selectedModel = result
            }
            binding.recyclerView2.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.recyclerView2.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        //老师导出学生选题信息
        binding.exportBtn.setOnClickListener {

        }
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}