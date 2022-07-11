package com.example.bmob.fragments.login.findPassword

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentPhoneNumberBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel

class PhoneNumberFragment : Fragment(),FragmentEventListener {
    private lateinit var binding: FragmentPhoneNumberBinding
    private val userViewModel:BmobUserViewModel by activityViewModels()
    private var phoneNumber = EMPTY_PHONE_NUMBER
    private val args:PhoneNumberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!userViewModel.isLogin()){
            userViewModel.getUserInfoByUsername(args.username){isSuccess, user, error ->
                if (isSuccess){
                    phoneNumber = user!!.mobilePhoneNumber
                    binding.phoneNumber = user.mobilePhoneNumber
                }else{
                    showMsg(requireContext(),"查找用户手机号失败")
                }
            }
        }else{
            phoneNumber = BmobUser.getCurrentUser().mobilePhoneNumber
            binding.phoneNumber = phoneNumber
        }
        setEventListener()
    }

    override fun setEventListener() {
        binding.nextBtn.setOnClickListener {
            val phone = binding.editText.text.toString()
            if (phone != EMPTY_PHONE_NUMBER){
                //smsId知识验证码的id，不是验证码
                userViewModel.findPassword(phone){isSuccess,smsId,error->
                    if (isSuccess){
                        Log.v(LOG_TAG,"PhoneNumberFragment请求验证码成功，code=$smsId code.toString=${smsId.toString()}")
                        findNavController().navigate(R.id.action_phoneNumberFragment_to_resetPasswordFragment)
                    }else{
                        showMsg(requireContext(),error.toString())
                    }
                }
            }
        }
    }
}

private const val EMPTY_PHONE_NUMBER = ""