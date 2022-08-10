package com.example.bmob.fragments.dean.select

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentStudentSelectedBinding
import com.example.bmob.databinding.ItemDeanStudentSelectBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.utils.JxlExcelUtil
import com.example.bmob.viewmodels.DeanStudentSelectedViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort


/**
 * 显示已选学生名单
 */
class StudentSelectedFragment :
    BaseFragment<DeanStudentSelectedViewModel, FragmentStudentSelectedBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        binding.dean = appUser
        binding.click = ProxyClick()
    }

    override fun createObserver() {
        viewModel.getStudentWhichHaveSelectedThesisLiveData(appUser, true) { msg ->
            showMsgShort(msg)
        }.observe(viewLifecycleOwner) { data -> initAdapter(data) }
    }

    inner class ProxyClick {
        fun onExport() {
            viewModel.getStudentWhichHaveSelectedThesisLiveData(appUser, true) {
                showMsgShort(it)
            }.value?.let {
                val excelFileName =
                    "${appUser.department} 已选课题学生名单${System.currentTimeMillis()}.xlsx"
                JxlExcelUtil.export(
                    it,
                    requireActivity(),
                    requireContext(),
                    excelFileName,
                    arrayOf("姓名", "年龄", "性别", "班级", "选课状态", "课题名称")
                ) { student ->
                    return@export arrayListOf<String>().apply {
                        add(student.name)
                        add(student.age.toString())
                        add(student.gender)
                        add(student.studentClass ?: "暂无班级")
                        add("已选")
                        add(student.studentThesis?.title!!)
                    }
                }
            }
        }
    }

    private fun initAdapter(data: MutableList<User>) {
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
        val adapter = RecyclerViewAdapter(data) { binding, result ->
            (binding as ItemDeanStudentSelectBinding).user = result
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
}