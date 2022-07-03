package com.example.bmob.fragments.login

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.databinding.FragmentRegisterBinding
import com.example.bmob.viewmodels.BmobUserViewModel


class RegisterFragment : Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    private val userViewModel:BmobUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupBth.setOnClickListener {

        }
    }
    private fun isAllInput():Boolean{
        return binding.let {
            TextUtils.isEmpty(it.nameEv.text)

        }
    }
}