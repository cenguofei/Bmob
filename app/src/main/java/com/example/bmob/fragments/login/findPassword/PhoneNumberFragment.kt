package com.example.bmob.fragments.login.findPassword

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.databinding.FragmentPhoneNumberBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString

class PhoneNumberFragment : BaseVbFragment<FragmentPhoneNumberBinding>() {
    private val userViewModel: BmobUserViewModel by activityViewModels()
    private var phoneNumber = EMPTY_PHONE_NUMBER
    private val args: PhoneNumberFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        if (!userViewModel.isLogin()) {
            userViewModel.getUserInfoByUsername(args.username) { isSuccess, user, error ->
                if (isSuccess) {
                    phoneNumber = user!!.mobilePhoneNumber
                    binding.phoneNumber = user.mobilePhoneNumber
                } else {
                    showMsgShort("查找用户手机号失败")
                }
            }
        } else {
            phoneNumber = BmobUser.getCurrentUser().mobilePhoneNumber
            binding.phoneNumber = phoneNumber
        }
    }

    override fun setEventListener() {
        binding.nextBtn.setOnClickListener {
            val phone = binding.editText.textString
            if (phone != EMPTY_PHONE_NUMBER) {
                //smsId知识验证码的id，不是验证码
                userViewModel.findPassword(phone) { isSuccess, smsId, error ->
                    if (isSuccess) {
                        Log.v(
                            LOG_TAG,
                            "PhoneNumberFragment请求验证码成功，code=$smsId code.toString=${smsId.toString()}"
                        )
                        findNavController().navigate(R.id.action_phoneNumberFragment_to_resetPasswordFragment)
                    } else {
                        showMsgShort(error.toString())
                    }
                }
            }
        }
    }
}

private const val EMPTY_PHONE_NUMBER = ""