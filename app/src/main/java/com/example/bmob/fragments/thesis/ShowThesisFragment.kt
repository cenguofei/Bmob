package com.example.bmob.fragments.thesis

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.data.entity.ALREADY_APPROVED
import com.example.bmob.databinding.FragmentShowThesisBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.myapp.appViewModel
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.MessageViewModel
import com.example.bmob.viewmodels.StudentSelectViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString

class ShowThesisFragment : BaseFragment<MessageViewModel, FragmentShowThesisBinding>() {
    private val args: ShowThesisFragmentArgs by navArgs()
    private val selectModel: StudentSelectViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        binding.thesis = args.thesis
        Log.v(LOG_TAG, "args.thesis:${args.thesis}")
        /**
         * 判断当前用户身份
         * 是学生则显示加入按钮，否则隐藏
         */
        if (args.isShowParticipateButton) {
            binding.participateButton.visibility = View.VISIBLE
            //判断时间
            selectModel.queryChooseThesisTime(appUser)
            { isSuccess, releaseTime, message ->
                if (isSuccess) {
                    binding.timeIntervalTv.visibility = View.VISIBLE
                    binding.releaseTime = releaseTime
                } else {
                    showMsgShort(message)
                }
            }

        } else {
            binding.participateButton.visibility = View.GONE
        }
    }

    override fun setEventListener() {
        binding.leaveMessageBtn.setOnClickListener {
            if (!TextUtils.isEmpty(binding.leaveMessageEt.text)) {
                Log.v(LOG_TAG, "留言thesis：${args.thesis}")
                viewModel.uploadMessage(
                    args.thesis,
                    binding.leaveMessageEt.textString,
                    appUser,
                    args.thesis.teacherId
                ) { _, message ->
                    showMsgShort(message)
                }

            } else showMsgShort("请输入留言内容")
        }
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.participateButton.setOnClickListener {
            //课题是否被审批
            if (args.thesis.thesisState == ALREADY_APPROVED) {
                //如果有时间区间，先检查当前时间是否在可选范围内
                if (selectModel.releaseTime != null) {
                    if (selectModel.isInSelectTime(selectModel.releaseTime!!)) {
                        selectModel.addStudentToTeacherThesis(appUser, args.thesis, { msg ->
                            showMsgShort(msg)
                        }) { stu ->
                            selectModel.fetchStudentThesis(stu) {
                                appViewModel.setUser(it)
                            }
                        }
                    } else showMsgShort("当前不是选题时间")
                } else showMsgShort("不是选题时间段")
            } else showMsgShort("课题未被审批或审批拒绝")
        }
    }
}