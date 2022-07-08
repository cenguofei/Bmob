package com.example.bmob.fragments.start

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_PROVOST
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.databinding.FragmentStartBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.BmobUserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*


class StartFragment : Fragment() {
    private lateinit var binding:FragmentStartBinding
    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore
    private val userViewModel:BmobUserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore.getInstance(requireContext())
        settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner){
            Timer().schedule(object :TimerTask(){  //给用户看2秒后跳转到对应页面
                override fun run() {
                    requireActivity().runOnUiThread {
                        if (!it.isRememberPassword){ //如果用户以前没有选择记住密码，不管是新用户还是老用户都需要登录
                            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                        }else{
                            //判断用户是否处于登录状态
                            //是
                            if (userViewModel.isLogin()){
                                val bmobUser = BmobUser.getCurrentUser()
                                Log.v(LOG_TAG,"已经登录:账号username：${bmobUser.username} 电话：${bmobUser.mobilePhoneNumber}")
                                userViewModel.getUserInfo { isSuccess, user ->
                                    if (isSuccess){
                                        userViewModel.getUserIdentificationAndNavigateForStart(user!!.identification,this@StartFragment)
                                    }
                                }
                            }else{ //不是
                                findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                            }
                        }
                    }
                }
                                                },2000)
        }
    }

//    /**
//     * 判断当前用户身份
//     */
//    fun getUserIdentificationAndNavigate(identification:Int){
//        when (identification) {
//            IDENTIFICATION_STUDENT -> {
//                findNavController().navigate(R.id.action_startFragment_to_studentHomeFragment)
//            }
//            IDENTIFICATION_TEACHER -> {
//                findNavController().navigate(R.id.action_loginFragment_to_teacherHomeFragment)
//            }
//            IDENTIFICATION_DEAN -> {
//                //
//            }
//            IDENTIFICATION_PROVOST -> {
//                //
//            }
//        }
//    }
}