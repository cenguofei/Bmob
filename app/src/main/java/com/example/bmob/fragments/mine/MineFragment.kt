package com.example.bmob.fragments.mine

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentMineBinding
import com.example.bmob.viewmodels.SetViewModel
import kotlinx.parcelize.Parcelize


class MineFragment : Fragment() ,FragmentEventListener{
    private lateinit var binding:FragmentMineBinding
    private val setViewModel:SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater,container,false)

        setViewModel.getBmobUser().observe(viewLifecycleOwner){
            binding.bmobUser = it
        }
        setViewModel.getUserByQuery().observe(viewLifecycleOwner){
            binding.user = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    //设置点击事件
    override fun setEventListener(){
        binding.editUserInfoIv.setOnClickListener {
            findNavController().navigate(R.id.action_mineFragment_to_setFragment)
        }
        //退出登录
        binding.reLogin.setOnClickListener {
            setViewModel.removeUser()
            BmobUser.logOut()
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }
        binding.exchangeAccount.setOnClickListener {
            setViewModel.removeUser()
            BmobUser.logOut()
            val actionMineFragmentToLoginFragment =
                MineFragmentDirections.actionMineFragmentToLoginFragment(true)
            findNavController().navigate(actionMineFragmentToLoginFragment)
        }
        binding.editUserInfoIv.setOnClickListener {
            setViewModel.getUserByQuery().value?.let {
                val actionMineFragmentToSetFragment =
                    MineFragmentDirections.actionMineFragmentToSetFragment(it)
                findNavController().navigate(actionMineFragmentToSetFragment)
            }
        }
    }
    companion object{
        const val QUERY_USER_KEY = "_query"
        const val BMOB_USER_KEY = "_bmob"
    }
}
