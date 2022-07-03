package com.example.bmob.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.data.entity.*
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.databinding.FragmentLoginBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private val userViewModel:BmobUserViewModel by viewModels()
    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore
    private var isShowPwd = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore.getInstance(requireContext())
        settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) {
            //设置CheckBox选择状态
            Log.v(LOG_TAG, it.toString())
            if (it.isRememberPassword){
                binding.userConfig = it
            }
        }
        setEventListener()
    }
    //识别用户身份
    private fun verifyUserIdentification(callback: (identification:Int) -> Unit){
        userViewModel.getUserInfo{isSuccess,user->
            lifecycleScope.launch {
                if (isSuccess){
                    settingsDataStore.saveIdentificationToPreferencesStore(user!!.identification ?: -1,requireContext())
                    callback.invoke(user.identification)
                }else{
                    settingsDataStore.saveIdentificationToPreferencesStore(USER_HAS_NOT_IDENTIFICATION,requireContext())
                    callback.invoke(USER_HAS_NOT_IDENTIFICATION)
                }
            }
        }
    }
    //获得用户输入的账号和密码
    private fun getUserInfo(callback:(String,String)->Unit){
        callback.invoke(binding.usernameEv.text.toString(),binding.passwordEv.text.toString())
    }
    //设置点击事件
    private fun setEventListener(){
        binding.showPwdIv.setOnClickListener {
            if (isShowPwd){
                //显示密码
                binding.showPwdIv.setImageResource(R.drawable.pwd_visible)
                binding.passwordEv.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                //隐藏密码
                binding.showPwdIv.setImageResource(R.drawable.pwd_invisible)
                binding.passwordEv.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            isShowPwd = !isShowPwd
        }
        with(binding.rememberPwdCheckBox) {
            setOnClickListener {
                getUserInfo{username,pwd->
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)){
                        lifecycleScope.launch{
                            if (isChecked) {
                                saveConfig(true,username,pwd)
                            }else{
                                saveConfig(false,"","")
                            }
                        }
                    }
                }
            }
        }
        binding.signupBth.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginBtn.setOnClickListener {
            getUserInfo{username,password->
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    showMsg(requireContext(),"用户名或密码不能为空！")
                }else{
                    userViewModel.loginByUsername(username,password){isSuccess,msg->
                        if (isSuccess){
                            verifyUserIdentification{
                                when(it){
                                    IDENTIFICATION_STUDENT->{
                                        //登录成功，进入学生主页
                                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                    }
                                    IDENTIFICATION_TEACHER->{

                                    }
                                    IDENTIFICATION_DEAN->{

                                    }
                                    IDENTIFICATION_PROVOST->{

                                    }
                                    else->{

                                    }
                                }
                            }
                        }else{
                            //登录失败
                            showMsg(requireContext(),msg)
                        }
                    }
                }
            }
        }
    }
    //保存用户配置
    private fun saveConfig(isRememberPassword:Boolean,username:String,password:String){
        lifecycleScope.launch {
            settingsDataStore.saveConfigToPreferencesStore(isRememberPassword,username,password,requireContext())
            Log.v(LOG_TAG,"保存成功")
        }
    }
}