package com.example.bmob.fragments.login.findPassword

import android.text.TextUtils
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.databinding.FragmentResetPasswordBinding
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString

class ResetPasswordFragment : BaseVbFragment<FragmentResetPasswordBinding>() {
    private val userViewModel: BmobUserViewModel by activityViewModels()

    override fun setEventListener() {
        binding.confirmBtn.setOnClickListener {
            val firstPwd = binding.editText.textString
            val secondPwd = binding.confirm.textString
            if (firstPwd == secondPwd) {
                if (TextUtils.isEmpty(binding.editTextCode.text)) {
                    showMsgShort("验证码不能为空")
                } else {
                    userViewModel.verifyCode(
                        binding.editTextCode.textString,
                        firstPwd
                    ) { isResetSuccess: Boolean, msg: String ->
                        if (isResetSuccess) {
                            showMsgShort("重置密码成功")
                            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                        } else {
                            showMsgShort("重置密码失败，请稍后重试.$msg")
                        }
                    }
                }
            } else {
                showMsgShort("两次输入的密码不同")
            }
        }
    }
}