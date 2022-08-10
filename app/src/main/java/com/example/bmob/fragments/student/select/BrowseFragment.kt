package com.example.bmob.fragments.student.select

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.BrowseTeacherHasThesisAdapter
import com.example.bmob.databinding.FragmentBrowseBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.StudentSelectViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort

/**
 * 浏览有课题的各个老师
 */
class BrowseFragment : BaseVbFragment<FragmentBrowseBinding>() {
    private val selectViewModel: StudentSelectViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        binding.user = appUser
    }

    override fun createObserver() {
        selectViewModel.getAllTeacherInDepartmentLiveData(appUser) {
            showMsgShort(it)
        }.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Log.v(LOG_TAG, "BrowseFragment查询可选课题的老师的课题成功了")
                val browseTeacherHasThesisAdapter =
                    BrowseTeacherHasThesisAdapter(it!!) { teacher ->
                        Log.v(LOG_TAG, "被点击：$teacher")
                        val actionBrowseFragmentToSelectFragment =
                            BrowseFragmentDirections.actionBrowseFragmentToSelectFragment(
                                teacher
                            )
                        findNavController().navigate(actionBrowseFragmentToSelectFragment)
                    }
                binding.recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL, false
                )
                binding.recyclerView.adapter = browseTeacherHasThesisAdapter
            }
        }
    }

    override fun setEventListener() {
        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}