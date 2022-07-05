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
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentMineBinding
import com.example.bmob.viewmodels.BmobUserViewModel


class MineFragment : Fragment() ,FragmentEventListener{
    private lateinit var binding:FragmentMineBinding
    private val userViewModel:BmobUserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater,container,false)
        setUserInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    //设置点击事件
    override fun setEventListener(){
        binding.reLogin.setOnClickListener {
            BmobUser.logOut()
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }
        binding.exchangeAccount.setOnClickListener {
            val actionMineFragmentToLoginFragment =
                MineFragmentDirections.actionMineFragmentToLoginFragment(true)
            findNavController().navigate(actionMineFragmentToLoginFragment)
        }
    }
    //设置用户的基本信息
    private fun setUserInfo(){
        userViewModel.getUserInfo{isSuccess, user ->
            if (isSuccess){
                binding.user = user
            }
        }
        val bmobUser = BmobUser.getCurrentUser()
        binding.bmobUser = bmobUser
    }
}