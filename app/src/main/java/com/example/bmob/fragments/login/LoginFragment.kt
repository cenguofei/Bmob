package com.example.bmob.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.databinding.FragmentLoginBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel

class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private val userViewModel:BmobUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    private fun setEventListener(){
        binding.signupBth.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginBtn.setOnClickListener {
            val username = binding.usernameEv.text.toString()
            val password = binding.passwordEv.text.toString()
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                showMsg(requireContext(),"用户名或密码不能为空！")
            }else{
                userViewModel.loginByUsername(username,password){isSuccess,msg->
                    if (isSuccess){
                        //用户是否选择记住密码
                        if (binding.rememberPwdCheckBox.isChecked){

                        }
                        //登录成功，进入主页
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }else{
                        //登录失败
                        showMsg(requireContext(),msg)
                    }
                }
            }
        }
    }
}