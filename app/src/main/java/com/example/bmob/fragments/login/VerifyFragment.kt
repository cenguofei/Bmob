package com.example.bmob.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.R
import com.example.bmob.databinding.FragmentVerifyBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel

class VerifyFragment : Fragment() {
    private lateinit var binding:FragmentVerifyBinding
    private val viewModel:BmobUserViewModel by activityViewModels()
    private val args:VerifyFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.beginVerify.setOnClickListener {
            val code1 = binding.editText1.text.toString()
            val code2 = binding.editText2.text.toString()
            val code3 = binding.editText3.text.toString()
            val code4 = binding.editText4.text.toString()
            val code5 = binding.editText5.text.toString()
            val code6 = binding.editText6.text.toString()
            if (
                TextUtils.isEmpty(code1)
                || TextUtils.isEmpty(code2)
                || TextUtils.isEmpty(code3)
                || TextUtils.isEmpty(code4)
                || TextUtils.isEmpty(code5)
                || TextUtils.isEmpty(code6)
            ){
                showMsg(requireContext(),"请输入完整的验证码")
            }else{
                Log.v(LOG_TAG,"codeVerifySuccessUser=${args.codeVerifySuccessUser}")
                args.codeVerifySuccessUser.run {
                    viewModel.signOrLogin(name,workNum,pwd,identification,phoneNum,
                        "$code1$code2$code3$code4$code5$code6",
                        args.codeVerifySuccessUser.school,
                        args.codeVerifySuccessUser.department,
                        args.codeVerifySuccessUser.code
                    ){ isSuccess, msg ->
                        if (isSuccess){
                            Log.v(LOG_TAG,"输入code=$code1$code2$code3$code4$code5$code6")
                            findNavController().navigate(R.id.action_verifyFragment_to_homeFragment)
                        }else{
                            showMsg(requireContext(),msg)
                        }
                    }
                }
            }
        }
    }
}