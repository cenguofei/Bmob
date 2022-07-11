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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.*
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.databinding.FragmentLoginBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import kotlinx.coroutines.launch


class LoginFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentLoginBinding
    private val userViewModel: BmobUserViewModel by activityViewModels()

    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore
    private var isShowPwd = true
    private val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore.getInstance(requireContext())
        if (args.isChangeAccount) {
            binding.passwordEv.setText(R.string.empty_text)
            binding.usernameEv.setText(R.string.empty_text)
        }
        else {
            settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) {
                //设置CheckBox选择状态
                Log.v(LOG_TAG, it.toString())
                if (it.isRememberPassword) {
                    binding.userConfig = it
                    //如果是记住密码并在有登录状态的情况下就进入首页
//                    if (userViewModel.isLogin()) {
//                        Log.v(LOG_TAG, "有登录状态，进入首页")
//                        findNavController().navigate(R.id.action_loginFragment_to_studentHomeFragment)
//                    }
                }
            }
        }
        setEventListener()
    }

    //识别用户身份
    private fun verifyUserIdentification(callback: (identification: Int) -> Unit) {
        userViewModel.getUserInfo { isSuccess, user ->
            lifecycleScope.launch {
                if (isSuccess) {
                    settingsDataStore.saveIdentificationToPreferencesStore(
                        user!!.identification, requireContext()
                    )
                    callback.invoke(user.identification)
                } else {
                    settingsDataStore.saveIdentificationToPreferencesStore(
                        USER_HAS_NOT_IDENTIFICATION,
                        requireContext()
                    )
                    callback.invoke(USER_HAS_NOT_IDENTIFICATION)
                }
            }
        }
    }

    //获得用户输入的账号和密码
    private fun getUserInputInfo(callback: (String, String) -> Unit) {
        callback.invoke(binding.usernameEv.text.toString(), binding.passwordEv.text.toString())
    }
    //设置点击事件
    override fun setEventListener() {
        binding.forgetPwd.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_usernameFragment)
        }
        binding.showPwdIv.setOnClickListener {
            if (isShowPwd) {
                //显示密码
                binding.showPwdIv.setImageResource(R.drawable.pwd_visible)
                binding.passwordEv.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                //隐藏密码
                binding.showPwdIv.setImageResource(R.drawable.pwd_invisible)
                binding.passwordEv.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            isShowPwd = !isShowPwd
        }
        with(binding.rememberPwdCheckBox) {
            setOnClickListener {
                getUserInputInfo { username, pwd ->
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
                        lifecycleScope.launch {
                            if (isChecked) {
                                saveConfig(true, username, pwd)
                            } else {
                                saveConfig(false, "", "")
                            }
                        }
                    }
                }
            }
        }

        //到注册页面
        binding.registerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        //需要判断并且保存身份
        binding.loginBtn.setOnClickListener {
            getUserInputInfo { username, password ->
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    showMsg(requireContext(), "用户名或密码不能为空！")
                } else {
                    userViewModel.loginByUsername(username, password) { isSuccess, msg ->
                        if (isSuccess) {
                            lifecycleScope.launch {
                                saveConfig(
                                    binding.rememberPwdCheckBox.isChecked,
                                    username,
                                    password
                                )
                            }
                            verifyUserIdentification {identification->
                                if (identification != USER_HAS_NOT_IDENTIFICATION){
                                    when (identification) {
                                        IDENTIFICATION_STUDENT -> {
                                            //登录成功，进入学生主页
                                            try {
                                                userViewModel.setUserIdentification(IDENTIFICATION_STUDENT)
                                                findNavController().navigate(R.id.action_loginFragment_to_studentHomeFragment)
                                            } catch (e: Exception) {
                                                Log.v(LOG_TAG, "登录出错：${e.message}")
                                                showMsg(requireContext(), "系统出错，请稍后再试")
                                            }
                                        }
                                        IDENTIFICATION_TEACHER -> {
                                            //登录成功，进入教师主页
                                            try {
                                                userViewModel.setUserIdentification(IDENTIFICATION_TEACHER)
                                                findNavController().navigate(R.id.action_loginFragment_to_teacherHomeFragment)
                                            } catch (e: Exception) {
                                                Log.v(LOG_TAG, "登录出错：${e.message}")
                                                showMsg(requireContext(), "系统出错，请稍后再试")
                                            }
                                        }
                                        IDENTIFICATION_DEAN -> {
                                            //登录成功，进入教师主页
                                            try {
                                                userViewModel.setUserIdentification(IDENTIFICATION_DEAN)
                                                findNavController().navigate(R.id.action_loginFragment_to_deanHomeFragment)
                                            } catch (e: Exception) {
                                                Log.v(LOG_TAG, "登录出错：${e.message}")
                                                showMsg(requireContext(), "系统出错，请稍后再试")
                                            }
                                        }
                                        IDENTIFICATION_PROVOST -> {
                                            //登录成功，进入教师主页
                                            try {
                                                userViewModel.setUserIdentification(IDENTIFICATION_PROVOST)
                                                findNavController().navigate(R.id.action_loginFragment_to_provostHomeFragment)
                                            } catch (e: Exception) {
                                                Log.v(LOG_TAG, "登录出错：${e.message}")
                                                showMsg(requireContext(), "系统出错，请稍后再试")
                                            }
                                        }
                                        else -> {
                                            showMsg(requireContext(),"系统出错")
                                        }
                                    }
                                }else{
                                    showMsg(requireContext(),"登录异常，请稍后再试")
                                }
                            }
                        } else {
                            //登录失败
                            showMsg(requireContext(), msg)
                        }
                    }
                }
            }
        }
    }

    //保存用户配置
    private fun saveConfig(isRememberPassword: Boolean, username: String, password: String) {
        lifecycleScope.launch {
            settingsDataStore.saveConfigToPreferencesStore(
                isRememberPassword,
                username,
                password,
                requireContext()
            )
            Log.v(LOG_TAG, "保存成功")
        }
    }
}