package com.example.bmob.fragments.student.oneThesis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.databinding.FragmentStudentThesisBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.myapp.appViewModel
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.StudentThesisViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort

class StudentThesisFragment : BaseFragment<StudentThesisViewModel, FragmentStudentThesisBinding>() {
    private val setViewModel: SetViewModel by activityViewModels()
    private var isSelectTime = false

    override fun initView(savedInstanceState: Bundle?) {
        if (appUser.studentSelectState) {
            if (appUser.studentThesis != null) {
                binding.student = appUser
                binding.thesis = appUser.studentThesis
                Log.v(LOG_TAG, "student的thesis：${appUser.studentThesis}")
            }
        } else showMsgShort("您还没有选择课题")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setViewModel.isSelectTime(appUser) { isSelectTime, _ ->
            if (isSelectTime) {
                this.isSelectTime = true
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setEventListener() {
        binding.outButton.setOnClickListener {
            if (isSelectTime) {
                viewModel.studentOutThesis(appUser) { isSuccess, student, message ->
                    showMsgShort(message)
                    if (isSuccess) {
                        appViewModel.setUser(student!!)
                    }
                }
            } else showMsgShort("当前不是退课时间")
        }

        binding.selectThesisBtn.setOnClickListener {
            if (isSelectTime) {
                findNavController().navigate(R.id.action_studentThesisFragment_to_browseFragment)
            } else showMsgShort("当前不是选题时间")
        }
    }
}