package com.example.bmob.fragments.login

import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.data.entity.*
import com.example.bmob.databinding.FragmentRegisterBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import kotlinx.parcelize.Parcelize


class RegisterFragment : Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    private val userViewModel:BmobUserViewModel by activityViewModels()
    private var userIdentification = USER_HAS_NOT_IDENTIFICATION

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    private fun setEventListener(){
        binding.accountLogin.setOnClickListener {
            findNavController().navigateUp()
        }
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
                    userViewModel.getSignupCode(phoneNum){isResponseSuccess, msgCode, msg ->
                        if (isResponseSuccess){
                            val navDirections = RegisterFragmentDirections
                                .actionRegisterFragmentToVerifyFragment(
                                    CodeVerifySuccessUser(name, workNum, phoneNum, pwd, msgCode,userIdentification)
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
    val identification:Int
): Parcelable