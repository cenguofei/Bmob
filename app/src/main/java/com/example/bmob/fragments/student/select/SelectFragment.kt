package com.example.bmob.fragments.student.select

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.databinding.FragmentSelectBinding
import com.example.bmob.databinding.StudentSelectThesisItemBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.StudentSelectViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort

/**
 * 显示一个老师的所有课题
 */
class SelectFragment : BaseVbFragment<FragmentSelectBinding>() {
    private val args: SelectFragmentArgs by navArgs()
    private val selectViewModel: StudentSelectViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        binding.thesisUser = args.teacher
    }

    override fun createObserver() {
        selectViewModel.getMutableTeacherThesisLiveData(args.teacher) {
            showMsgShort(it)
        }.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "getMutableTeacherThesisLiveData 成功：$it")
            RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
                val itemInflater = LayoutInflater.from(parent.context)
                RecyclerViewAdapter.ResultViewHolder(
                    StudentSelectThesisItemBinding.inflate(
                        itemInflater,
                        parent,
                        false
                    )
                )
            }
            val adapter = RecyclerViewAdapter(it) { binding, result ->
                (binding as StudentSelectThesisItemBinding).run {
                    thesis = result
                    root.setOnClickListener {
                        val actionSelectFragmentToShowThesisFragment =
                            SelectFragmentDirections
                                .actionSelectFragmentToShowThesisFragment(result, true)
                        /**
                         * 设置为true，通知ShowThesisFragment，
                         *
                         * 判断身份
                         */
                        selectViewModel.isStudentSelectThesis.value = true
                        findNavController().navigate(actionSelectFragmentToShowThesisFragment)
                    }
                }
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL, false
            )
            binding.recyclerView.adapter = adapter
        }
    }

    override fun setEventListener() {
        binding.backImg.setOnClickListener { findNavController().navigateUp() }
    }
}