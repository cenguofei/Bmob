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
import com.example.bmob.utils.JxlExcelUtil
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SelectedModel
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.TeacherSelectResultViewModel


class TeacherSelectResultFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentTeacherSelectResultBinding
    private val viewModel: TeacherSelectResultViewModel by viewModels()

    //一定要用全局的SetViewModel，不然会空指针
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherSelectResultBinding.inflate(inflater, container, false)
        viewModel.getStudentOfSelectedThisThesisLiveData(setViewModel.getUserByQuery().value!!) {
            showMsg(requireContext(), it)
        }.observe(viewLifecycleOwner) { initAdapter(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        //老师导出学生选题信息
        binding.exportBtn.setOnClickListener {
            viewModel.getStudentOfSelectedThisThesisLiveData(setViewModel.getUserByQuery().value!!) {
                showMsg(requireContext(), it)
            }.value?.let { data ->
                val teacher = setViewModel.getUserByQuery().value
                val excelFileName = "${teacher?.name}教师课题被选信息${System.currentTimeMillis()}.xlsx"
                JxlExcelUtil.export(
                    data,
                    requireActivity(),
                    requireContext(),
                    excelFileName,
                    arrayOf("被选课题", "学生所在系", "学生班级", "学生姓名")
                ) { selectedModel ->
                    return@export arrayListOf<String>().apply {
                        add(selectedModel.title)
                        add(selectedModel.department)
                        add(selectedModel.studentClass.ifEmpty { "暂无班级" })
                        add(selectedModel.studentName)
                    }
                }
            }
        }
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initAdapter(selectedModelList: MutableList<SelectedModel>){
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
        val adapter = RecyclerViewAdapter(selectedModelList) { binding, result ->
            (binding as StudentChooseThesisItemBinding).selectedModel = result
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
}