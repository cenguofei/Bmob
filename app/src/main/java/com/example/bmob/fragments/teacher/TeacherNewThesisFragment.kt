package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentTeacherNewThesisBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.TeacherThesisViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString

class TeacherNewThesisFragment : BaseVbFragment<FragmentTeacherNewThesisBinding>() {
    private val viewModel: TeacherThesisViewModel by activityViewModels()
    private val args: TeacherNewThesisFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        binding.teacherAvatarUrl = appUser.avatarUrl

        if (args.isUpdate) {
            binding.updateButton.visibility = View.VISIBLE
            binding.ensureButton.visibility = View.GONE
        }

        binding.click = ProxyClick()
    }

    override fun createObserver() {
        viewModel.selectedThesis.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "教师选中thesis：$it")
            if (args.isUpdate) {
                binding.thesis = it
            }
        }
    }

    inner class ProxyClick {
        //确定保存可以
        fun onEnsure() {
            try {
                //仅当用户身份为教师时才能上传
                if (viewModel.isInputValid(binding)) {
                    viewModel.saveThesis(
                        appUser,
                        Thesis(
                            title = binding.thesisTitle.textString,
                            field = binding.thesisField.textString,
                            require = binding.thesisRequire.textString,
                            description = binding.thesisBrief.textString
                        )
                    ) { isSuccess, msg ->
                        if (isSuccess) {
                            showMsgShort("课题上传成功")
                            back()
                        } else {
                            showMsgShort("课题上传失败，请稍后再试:$msg")
                        }
                    }
                } else {
                    showMsgShort("请完善信息")
                }

            } catch (e: Exception) {
                Log.v(LOG_TAG, "TeacherNewThesis出错了：${e.message}")
            }
        }

        //取消课题
        fun onCancel() {
            back()
        }

        fun onUpdate() {
            val title = binding.thesisTitle.textString
            val field = binding.thesisField.textString
            val require = binding.thesisRequire.textString
            val description = binding.thesisBrief.textString
            viewModel.selectedThesis.value?.let {
                if (
                    !TextUtils.equals(title, it.title) ||
                    !TextUtils.equals(field, it.field) ||
                    !TextUtils.equals(require, it.require) ||
                    !TextUtils.equals(description, it.description)
                ) {
                    if (viewModel.isInputValid(binding)) {
                        it.title = title
                        it.field = field
                        it.require = require
                        it.description = description
                        viewModel.updateThesis(user = appUser, thesis = it) { isSuccess, msg ->
                            if (isSuccess) {
                                showMsgShort("更新成功")
                            } else {
                                showMsgShort("更新失败：$msg")
                            }
                        }
                    } else {
                        showMsgShort("请完善信息")
                    }
                }
            }
        }
    }

    private fun back() {
        findNavController().navigateUp()
    }
}