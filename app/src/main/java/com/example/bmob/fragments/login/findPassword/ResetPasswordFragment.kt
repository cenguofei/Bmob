package com.example.bmob.fragments.login.findPassword

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentResetPasswordBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel

class ResetPasswordFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentResetPasswordBinding
    private val userViewModel: BmobUserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.confirmBtn.setOnClickListener {
            val firstPwd = binding.editText.text.toString()
            val secondPwd = binding.confirm.text.toString()
            if (firstPwd == secondPwd){
                if (TextUtils.isEmpty(binding.editTextCode.text)){
                    showMsg(requireContext(),"验证码不能为空")
                }else{
                    userViewModel.verifyCode(binding.editTextCode.text.toString(),firstPwd){isResetSuccess: Boolean, msg: String ->
                        if (isResetSuccess){
                            showMsg(requireContext(),"重置密码成功")
                            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                        }else{
                            showMsg(requireContext(),"重置密码失败，请稍后重试.$msg")
                        }
                    }
                }
            }else{
                showMsg(requireContext(),"两次输入的密码不同")
            }
        }
    }
}