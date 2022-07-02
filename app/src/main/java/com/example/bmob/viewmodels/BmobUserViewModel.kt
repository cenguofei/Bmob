package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bmob.data.repository.remote.BmobUserRepository

class BmobUserViewModel:ViewModel() {
    companion object{
        private val userRepository = BmobUserRepository.getInstance()
    }
    fun loginByUsername(userName:String,pwd:String,callback: (Boolean)->Unit){
        userRepository.loginByUsername(userName,pwd,callback)
    }

}