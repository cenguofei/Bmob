package com.example.bmob.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.databinding.FragmentVerifyBinding
import com.example.bmob.myapp.appViewModel
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.logI
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString


class VerifyFragment : BaseVbFragment<FragmentVerifyBinding>() {
    private val viewModel: BmobUserViewModel by activityViewModels()
    private val args: VerifyFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        showSoftInputFromWindow(binding.editText1)
    }

    /**
     * EditText获取焦点弹出软键盘
     */
    private fun showSoftInputFromWindow(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputManager: InputMethodManager =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
    }

    override fun setEventListener() {
        with(binding) {
            editText1.doOnTextChanged { _, _, _, count ->
                if (count == 1) {
                    editText1.clearFocus()
                    editText2.requestFocus()
                }
            }
            editText2.doOnTextChanged { _, _, _, count ->
                if (count == 1) {
                    editText2.clearFocus()
                    editText3.requestFocus()
                }
            }
            editText3.doOnTextChanged { _, _, _, count ->
                if (count == 1) {
                    editText3.clearFocus()
                    editText4.requestFocus()
                }
            }
            editText4.doOnTextChanged { _, _, _, count ->
                if (count == 1) {
                    editText4.clearFocus()
                    editText5.requestFocus()
                }
            }
            editText5.doOnTextChanged { _, _, _, count ->
                if (count == 1) {
                    editText5.clearFocus()
                    editText6.requestFocus()
                }
            }
        }

        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.beginVerify.setOnClickListener {
            val code1 = binding.editText1.textString
            val code2 = binding.editText2.textString
            val code3 = binding.editText3.textString
            val code4 = binding.editText4.textString
            val code5 = binding.editText5.textString
            val code6 = binding.editText6.textString
            if (
                TextUtils.isEmpty(code1)
                || TextUtils.isEmpty(code2)
                || TextUtils.isEmpty(code3)
                || TextUtils.isEmpty(code4)
                || TextUtils.isEmpty(code5)
                || TextUtils.isEmpty(code6)
            ) {
                showMsgShort("请输入完整的验证码")
            } else {
                Log.v(LOG_TAG, "codeVerifySuccessUser=${args.codeVerifySuccessUser}")
                args.codeVerifySuccessUser.run {
                    viewModel.signOrLogin(
                        name, workNum, pwd, identification, phoneNum,
                        "$code1$code2$code3$code4$code5$code6",
                        args.codeVerifySuccessUser.school,
                        args.codeVerifySuccessUser.department,
                        args.codeVerifySuccessUser.college, { isSuccess, msg ->
                            if (isSuccess) {
                                //这里需要识别身份再进入对应主页
                                //识别身份
                                Log.v(LOG_TAG, "输入code=$code1$code2$code3$code4$code5$code6")
                                viewModel.getUserIdentificationAndNavigateFromVerify(
                                    identification,
                                    this@VerifyFragment
                                )
                                "*appViewModel 在VerifyFragment 设置 $identification".logI()
                            } else {
                                showMsgShort(msg)
                            }
                        }, {
                            appViewModel.setUser(it)
                        })
                }
            }
        }
    }
}