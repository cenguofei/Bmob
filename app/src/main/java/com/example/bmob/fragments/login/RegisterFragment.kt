package com.example.bmob.fragments.login

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.data.entity.*
import com.example.bmob.databinding.FragmentRegisterBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString
import kotlinx.parcelize.Parcelize


class RegisterFragment : BaseVbFragment<FragmentRegisterBinding>() {
    private val userViewModel: BmobUserViewModel by activityViewModels()
    private var userIdentification = USER_HAS_NOT_IDENTIFICATION
    private var departments = emptyList<String>()
    private var colleges = emptyList<String>()
    private var finalInput = EMPTY_TEXT

    override fun initView(savedInstanceState: Bundle?) {
        setInitialVisibility()
        adjustEditTextDrawableStart(binding.schoolEv, R.drawable.school)
        adjustEditTextDrawableStart(binding.departmentEv, R.drawable.department)
        adjustEditTextDrawableStart(binding.collegeEv, R.drawable.college)
        binding.click = ProxyClick()
    }

    private fun setInitialVisibility() {
        binding.departmentEv.visibility = View.GONE
        binding.collegeEv.visibility = View.GONE
    }

    /**
     * 调整图片大小
     */
    private fun adjustEditTextDrawableStart(editText: EditText, drawableId: Int) {
        val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
        drawable!!.setBounds(0, 0, 55, 55)
        editText.setCompoundDrawables(drawable, null, null, null)
    }

    inner class ProxyClick {
        fun onBack() {
            findNavController().navigateUp()
        }

        fun onProvostLinearLayout() {
            userIdentification = IDENTIFICATION_PROVOST
            binding.currentIdentification.text = "当前身份为：教务长"
        }

        fun onStudentLinearLayout() {
            userIdentification = IDENTIFICATION_STUDENT
            binding.currentIdentification.text = "当前身份为：学生"
        }

        fun onDeanLinearLayout() {
            userIdentification = IDENTIFICATION_DEAN
            binding.currentIdentification.text = "当前身份为：系主任"
        }

        fun onTeacherLinearLayout() {
            userIdentification = IDENTIFICATION_TEACHER
            binding.currentIdentification.text = "当前身份为：老师"
        }

        fun onRegister() {
            if (userIdentification != USER_HAS_NOT_IDENTIFICATION) {
                if (isAllInputInvalid()) {
                    val name = binding.nameEv.textString
                    val workNum = binding.workNumEv.textString
                    val phoneNum = binding.phoneNumberEv.textString
                    val pwd = binding.pwdEv.textString

                    val school = binding.schoolEv.textString
                    val department = binding.departmentEv.textString
                    val college = binding.collegeEv.textString

                    userViewModel.getSignupCode(phoneNum) { isResponseSuccess, msgCode, msg ->
                        if (isResponseSuccess) {
                            val navDirections = RegisterFragmentDirections
                                .actionRegisterFragmentToVerifyFragment(
                                    CodeVerifySuccessUser(
                                        name,
                                        workNum,
                                        phoneNum,
                                        pwd,
                                        msgCode,
                                        userIdentification,
                                        school,
                                        department,
                                        college
                                    )
                                )
                            findNavController().navigate(navDirections)
                        } else {
                            showMsgShort(msg)
                        }
                    }
                } else {
                    showMsgShort("请完善信息")
                }
            } else {
                showMsgShort("请选择身份")
            }
        }
    }

    override fun setEventListener() {
        binding.schoolEv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.departmentEv.visibility = View.GONE
                binding.collegeEv.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                finalInput = EMPTY_TEXT
                val inputSchool = s.toString()
                userViewModel.querySchool(inputSchool) { isSuccess, school, error ->
                    if (isSuccess) {
                        if (school!!.schoolName == inputSchool) {
                            departments = school.departments
                            colleges = school.college
                            binding.collegeEv.visibility = View.VISIBLE
                            binding.isValidTextView.text = "输入学校合法"
                            Log.v(LOG_TAG, "departs=$departments \ncolleges=$colleges\n")
                        } else {
                            binding.collegeEv.visibility = View.GONE
                            binding.isValidTextView.text = "输入的学校不存在"
                        }
                    } else {
                        showMsgShort(error)
                    }
                }
            }
        })
        binding.collegeEv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.departmentEv.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                finalInput = EMPTY_TEXT

                var flag = false
                colleges.forEach {
                    if (s.toString() == it) {
                        binding.isValidTextView.text = "输入的院合法"
                        flag = true
                        binding.departmentEv.visibility = View.VISIBLE
                    }
                }
                if (!flag) {
                    binding.isValidTextView.text = "输入的院不在该学校"
                    binding.departmentEv.visibility = View.GONE
                }
            }
        })
        binding.departmentEv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                finalInput = EMPTY_TEXT
                var flag = false
                departments.forEach {
                    if (s.toString() == it) {
                        binding.isValidTextView.text = "输入的系合法"
                        flag = true
                        finalInput = FINAL_VALID_INPUT
                    }
                }
                if (!flag) {
                    binding.isValidTextView.text = "输入的系不在该院"
                    finalInput = EMPTY_TEXT
                }
            }
        })
    }

    private fun isAllInputInvalid(): Boolean {
        return binding.let {
            !TextUtils.isEmpty(it.nameEv.text)
                    && !TextUtils.isEmpty(it.workNumEv.text)
                    && !TextUtils.isEmpty(it.phoneNumberEv.text)
                    && !TextUtils.isEmpty(it.pwdEv.text)
                    && finalInput == FINAL_VALID_INPUT
        }
    }
}

@Parcelize
data class CodeVerifySuccessUser(
    val name: String,
    val workNum: String,
    val phoneNum: String,
    val pwd: String,
    val code: String,
    val identification: Int,
    val school: String,
    val department: String,
    val college: String
) : Parcelable


private const val FINAL_VALID_INPUT = "depart_"
private const val EMPTY_TEXT = ""