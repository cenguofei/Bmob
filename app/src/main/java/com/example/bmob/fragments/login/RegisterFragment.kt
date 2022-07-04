package com.example.bmob.fragments.login

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.data.entity.*
import com.example.bmob.databinding.FragmentRegisterBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import kotlinx.parcelize.Parcelize


class RegisterFragment : Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    private val userViewModel:BmobUserViewModel by activityViewModels()
    private var userIdentification = USER_HAS_NOT_IDENTIFICATION
    private var departments = emptyList<String>()
    private var colleges = emptyList<String>()
    private var finalInput = EMPTY_TEXT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialVisibility()
        adjustEditTextDrawableStart(binding.schoolEv,R.drawable.school)
        adjustEditTextDrawableStart(binding.departmentEv,R.drawable.department)
        adjustEditTextDrawableStart(binding.collegeEv,R.drawable.college)
        setEventListener()
    }
    private fun  setInitialVisibility(){
        binding.departmentEv.visibility = View.GONE
        binding.collegeEv.visibility = View.GONE
    }

    /**
     * 调整图片大小
     */
    private fun adjustEditTextDrawableStart(editText: EditText,drawableId:Int){
        val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
        drawable!!.setBounds(0,0,55,55)
        editText.setCompoundDrawables(drawable,null,null,null)
    }
    private fun setEventListener(){
        binding.schoolEv.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.v(LOG_TAG,"编辑学校beforeTextChanged：s:${s} start:$start  count:$count")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Log.v(LOG_TAG,"编辑学校onTextChanged：s:${s} start:$start  count:$count")
                binding.departmentEv.visibility = View.GONE
                binding.collegeEv.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                finalInput = EMPTY_TEXT
                Log.v(LOG_TAG,"编辑学校afterTextChanged：s:${s.toString()}")
                val inputSchool = s.toString()
                userViewModel.querySchool(inputSchool){isSuccess, school, error ->
                    if (isSuccess){
                        if (school!!.schoolName == inputSchool){
                            departments = school.departments
                            colleges = school.college
                            binding.departmentEv.visibility = View.VISIBLE
                            binding.isValidTextView.text = "输入学校合法"
                        }else{
                            binding.departmentEv.visibility = View.GONE
                            binding.isValidTextView.text = "输入的学校不存在"
                        }
                    }else{
                        showMsg(requireContext(),error)
                    }
                }
            }
        })
        binding.departmentEv.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.collegeEv.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                finalInput = EMPTY_TEXT
                var flag = false
                departments.forEach {
                    if (s.toString() == it){
                        binding.isValidTextView.text = "输入的系合法"
                        flag = true
                        binding.collegeEv.visibility = View.VISIBLE
                    }
                }
                if (!flag){
                    binding.isValidTextView.text = "输入的系不在该学校"
                    binding.collegeEv.visibility = View.GONE
                }
            }
        })
        binding.collegeEv.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var flag = false
                colleges.forEach {
                    if (s.toString() == it){
                        binding.isValidTextView.text = FINAL_VALID_INPUT
                        flag = true
                        finalInput = FINAL_VALID_INPUT
                    }
                }
                if (!flag){
                    binding.isValidTextView.text = "输入的院不在该学校"
                    finalInput = EMPTY_TEXT
                }
            }
        })
        binding.provostLinearLayout.run {
            setOnClickListener {
                userIdentification = IDENTIFICATION_PROVOST
                binding.currentIdentification.text = "当前身份为：教务长"
            }
        }
        binding.studentLinearLayout.run {
            setOnClickListener {
                userIdentification = IDENTIFICATION_STUDENT
                binding.currentIdentification.text = "当前身份为：学生"
            }
        }
        binding.deanLinearLayout.run {
            setOnClickListener {
                userIdentification = IDENTIFICATION_DEAN
                binding.currentIdentification.text = "当前身份为：系主任"
            }
        }
        binding.teacherLinearLayout.run {
            setOnClickListener {
                userIdentification = IDENTIFICATION_TEACHER
                binding.currentIdentification.text = "当前身份为：老师"
            }
        }
        binding.registerBtn.setOnClickListener {
            if (userIdentification != USER_HAS_NOT_IDENTIFICATION){
                if (isAllInputInvalid()){
                    val name = binding.nameEv.text.toString()
                    val workNum = binding.workNumEv.text.toString()
                    val phoneNum = binding.phoneNumberEv.text.toString()
                    val pwd = binding.pwdEv.text.toString()

                    val school = binding.schoolEv.text.toString()
                    val department = binding.departmentEv.text.toString()
                    val college = binding.collegeEv.text.toString()

                    userViewModel.getSignupCode(phoneNum){isResponseSuccess, msgCode, msg ->
                        if (isResponseSuccess){
                            val navDirections = RegisterFragmentDirections
                                .actionRegisterFragmentToVerifyFragment(
                                    CodeVerifySuccessUser(name, workNum, phoneNum, pwd, msgCode,userIdentification,school,department,college)
                                )
                            findNavController().navigate(navDirections)
                        }else{
                            showMsg(requireContext(),msg)
                        }
                    }
                }else{
                    showMsg(requireContext(),"请完善信息")
                }
            }else{
                showMsg(requireContext(),"请选择身份")
            }
        }
    }
    private fun isAllInputInvalid():Boolean{
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
    val name:String,
    val workNum:String,
    val phoneNum:String,
    val pwd:String,
    val code:String,
    val identification:Int,
    val school:String,
    val department:String,
    val college:String
): Parcelable


private const val FINAL_VALID_INPUT = "输入的院合法"
private const val EMPTY_TEXT = ""