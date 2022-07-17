package com.example.bmob.fragments.dean.select

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentStudentSelectedBinding
import com.example.bmob.databinding.ItemDeanStudentSelectBinding
import com.example.bmob.utils.JxlExcelUtil
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.DeanStudentSelectedViewModel
import com.example.bmob.viewmodels.SetViewModel
import java.io.File


/**
 * 显示已选学生名单
 */
class StudentSelectedFragment : Fragment() ,FragmentEventListener {
    private lateinit var binding: FragmentStudentSelectedBinding
    private val viewModel: DeanStudentSelectedViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentSelectedBinding.inflate(inflater, container, false)
        binding.dean = setViewModel.getUserByQuery().value
        viewModel.getStudentWhichHaveSelectedThesisLiveData(
            setViewModel.getUserByQuery().value!!,
            true
        ) {
            showMsg(requireContext(), it)
        }.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
                    val itemInflater = LayoutInflater.from(parent.context)
                    RecyclerViewAdapter.ResultViewHolder(
                        ItemDeanStudentSelectBinding.inflate(
                            itemInflater,
                            parent,
                            false
                        )
                    )
                }
                val adapter = RecyclerViewAdapter(it) { binding, result ->
                    (binding as ItemDeanStudentSelectBinding).user = result
                }
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                binding.recyclerView.adapter = adapter
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.exportButton.setOnClickListener {
            viewModel.getStudentWhichHaveSelectedThesisLiveData(
                setViewModel.getUserByQuery().value!!,
                true
            ) {
                showMsg(requireContext(), it)
            }.value?.let {
                val dean = setViewModel.getUserByQuery().value
                val excelFileName = "${dean?.department} 已选课题学生名单${System.currentTimeMillis()}.xlsx"
                JxlExcelUtil.export(
                    it,
                    requireActivity(),
                    requireContext(),
                    excelFileName,
                    arrayOf("姓名", "年龄", "性别", "班级", "选课状态", "课题名称")
                ) {student->
                    return@export arrayListOf<String>().apply {
                        add(student.name!!)
                        add(student.age.toString())
                        add(student.gender!!)
                        add(student.studentClass ?: "暂无班级")
                        add("已选")
                        add(student.studentThesis?.title!!)
                    }
                }
            }
        }
    }
}