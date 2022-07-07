package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentTeacherSelectResultBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.TeacherSelectResultViewModel


class TeacherSelectResultFragment : Fragment(),FragmentEventListener{
    private lateinit var binding:FragmentTeacherSelectResultBinding
    private val viewModel:TeacherSelectResultViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherSelectResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
        viewModel.getStudentOfSelectedThisThesis{isSuccess, selectedModelList, msg ->
            if (isSuccess){
                val adapter = SelectedAdapter(selectedModelList!!)
                binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                binding.recyclerView2.adapter = adapter
            }else{
                showMsg(requireContext(),msg)
            }
        }
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