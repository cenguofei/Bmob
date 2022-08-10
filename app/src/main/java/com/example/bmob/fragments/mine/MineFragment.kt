package com.example.bmob.fragments.mine

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.databinding.FragmentMineBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.myapp.appViewModel
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment


class MineFragment : BaseVbFragment<FragmentMineBinding>() {
    private val setViewModel: SetViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        binding.click = ProxyClick()
    }

    override fun createObserver() {
        setViewModel.getBmobUser().observe(viewLifecycleOwner) {
            binding.bmobUser = it
        }
        appViewModel.user.observe(viewLifecycleOwner) {
            binding.user = it
        }
    }

    inner class ProxyClick {
        fun onEditUserInfo() {
            val actionMineFragmentToSetFragment =
                MineFragmentDirections.actionMineFragmentToSetFragment(appUser)
            findNavController().navigate(actionMineFragmentToSetFragment)
        }

        //退出登录
        fun onReLogin() {
            setViewModel.removeUser()
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }

        fun onExchangeAccount() {
            setViewModel.removeUser()
            val actionMineFragmentToLoginFragment =
                MineFragmentDirections.actionMineFragmentToLoginFragment(true)
            findNavController().navigate(actionMineFragmentToLoginFragment)
        }
    }

    companion object {
        const val QUERY_USER_KEY = "_query"
        const val BMOB_USER_KEY = "_bmob"
    }
}
