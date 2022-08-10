package com.example.bmob.fragments.teacher

import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.databinding.FragmentTeacherSelectResultBinding
import com.example.bmob.databinding.StudentChooseThesisItemBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.utils.JxlExcelUtil
import com.example.bmob.viewmodels.SelectedModel
import com.example.bmob.viewmodels.TeacherSelectResultViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort


class TeacherSelectResultFragment :
    BaseFragment<TeacherSelectResultViewModel, FragmentTeacherSelectResultBinding>() {

    override fun createObserver() {
        viewModel.getStudentOfSelectedThisThesisLiveData(appUser) {
            showMsgShort(it)
        }.observe(viewLifecycleOwner) { initAdapter(it) }
    }

    override fun setEventListener() {
        //老师导出学生选题信息
        binding.exportBtn.setOnClickListener {
            viewModel.getStudentOfSelectedThisThesisLiveData(appUser) {
                showMsgShort(it)
            }.value?.let { data ->
                val teacher = appUser
                val excelFileName = "${teacher.name}教师课题被选信息${System.currentTimeMillis()}.xlsx"
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

    private fun initAdapter(selectedModelList: MutableList<SelectedModel>) {
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