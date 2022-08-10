package com.example.bmob.fragments.login.findPassword

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.databinding.FragmentUsernameBinding
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString

class UsernameFragment : BaseVbFragment<FragmentUsernameBinding>() {
    private val userViewModel: BmobUserViewModel by activityViewModels()
    private var username = EMPTY_USERNAME

    override fun initView(savedInstanceState: Bundle?) {
        if (userViewModel.isLogin()) {
            username = BmobUser.getCurrentUser().username.toString()
            binding.username = username
        }
    }

    override fun setEventListener() {
        binding.nextBtn.setOnClickListener {
            val text = binding.editText.text
            if (TextUtils.isEmpty(text)) {
                showMsgShort("请输入账号")
            } else {
                userViewModel.ifExistUserForGivenUsername(text.textString) { isExist, msg ->
                    if (isExist) {
                        val navDirections =
                            UsernameFragmentDirections.actionUsernameFragmentToPhoneNumberFragment(
                                text.textString
                            )
                        findNavController().navigate(navDirections)
                    } else {
                        showMsgShort(msg)
                    }
                }
            }
        }
    }
}

private const val EMPTY_USERNAME = ""