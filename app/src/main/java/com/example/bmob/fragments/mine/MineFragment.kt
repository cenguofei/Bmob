package com.example.bmob.fragments.mine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.databinding.FragmentMineBinding
import com.example.bmob.viewmodels.BmobUserViewModel


class MineFragment : Fragment() {
    private lateinit var binding:FragmentMineBinding
    private val userViewModel:BmobUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater,container,false)
        userViewModel.getUserInfo{isSuccess, user ->
            if (isSuccess){
                binding.user = user
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reLogin.setOnClickListener {
            BmobUser.logOut()
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }
    }
}