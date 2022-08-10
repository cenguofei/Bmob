package com.example.bmob.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.R
import com.example.bmob.data.entity.*
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.databinding.FragmentLoginBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.getSettingsDataStore
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.logI
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString
import kotlinx.coroutines.launch


class LoginFragment : BaseVbFragment<FragmentLoginBinding>() {
    private val userViewModel: BmobUserViewModel by activityViewModels()

    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore
    private var isShowPwd = true
    private val args: LoginFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        binding.click = ProxyClick()
        if (args.isChangeAccount) {
            binding.passwordEv.setText(R.string.empty_text)
            binding.usernameEv.setText(R.string.empty_text)
        }
    }

    override fun createObserver() {
        if (!args.isChangeAccount) {
            settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) {
                //设置CheckBox选择状态
                Log.v(LOG_TAG, it.toString())
                if (it.isRememberPassword) {
                    binding.userConfig = it
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataStore = getSettingsDataStore()
    }

    //识别用户身份
    private fun verifyUserIdentification(callback: (identification: Int) -> Unit) {
        userViewModel.getUserInfo { isSuccess, user ->
            appViewModel.setUser(user!!)
            lifecycleScope.launch {
                if (isSuccess) {
                    settingsDataStore.saveIdentificationToPreferencesStore(
                        user.identification, requireContext()
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

    inner class ProxyClick {
        fun onForgetPwd() {
            findNavController().navigate(R.id.action_loginFragment_to_usernameFragment)
        }

        fun onShowPwd() {
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

        fun onRememberPwd() {
            val username = binding.usernameEv.textString
            val pwd = binding.passwordEv.textString
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
                lifecycleScope.launch {
                    if (binding.rememberPwdCheckBox.isChecked) {
                        saveConfig(true, username, pwd)
                    } else {
                        saveConfig(false, "", "")
                    }
                }
            }
        }

        //到注册页面
        fun onRegister() {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        //需要判断并且保存身份
        fun onLogin() {
            val username = binding.usernameEv.textString
            val password = binding.passwordEv.textString
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                showMsgShort("用户名或密码不能为空！")
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
                        verifyUserIdentification { identification -> loginToHome(identification) }
                    } else {
                        //登录失败
                        showMsgShort(msg)
                    }
                }
            }
        }
    }

    private fun loginToHome(identification: Int) {
        when (identification) {
            IDENTIFICATION_STUDENT -> {
                //登录成功，进入学生主页
                try {
                    appViewModel.userIdentification.value = IDENTIFICATION_STUDENT
                    findNavController().navigate(R.id.action_loginFragment_to_studentHomeFragment)
                } catch (e: Exception) {
                    Log.v(LOG_TAG, "登录出错：${e.message}")
                    showMsgShort("系统出错，请稍后再试")
                }
            }
            IDENTIFICATION_TEACHER -> {
                //登录成功，进入教师主页
                try {
                    appViewModel.userIdentification.value = IDENTIFICATION_TEACHER
                    findNavController().navigate(R.id.action_loginFragment_to_teacherHomeFragment)
                } catch (e: Exception) {
                    Log.v(LOG_TAG, "登录出错：${e.message}")
                    showMsgShort("系统出错，请稍后再试")
                }
            }
            IDENTIFICATION_DEAN -> {
                //登录成功，进入教师主页
                try {
                    appViewModel.userIdentification.value = IDENTIFICATION_DEAN
                    findNavController().navigate(R.id.action_loginFragment_to_deanHomeFragment)
                } catch (e: Exception) {
                    Log.v(LOG_TAG, "登录出错：${e.message}")
                    showMsgShort("系统出错，请稍后再试")
                }
            }
            IDENTIFICATION_PROVOST -> {
                //登录成功，进入教师主页
                try {
                    appViewModel.userIdentification.value = IDENTIFICATION_PROVOST
                    findNavController().navigate(R.id.action_loginFragment_to_provostHomeFragment)
                } catch (e: Exception) {
                    Log.v(LOG_TAG, "登录出错：${e.message}")
                    showMsgShort("系统出错，请稍后再试")
                }
            }
            else -> {
                showMsgShort("登录异常，请稍后再试")
            }
        }
        "*appViewModel 在LoginFragment 设置 $identification".logI()
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