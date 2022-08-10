package com.example.bmob.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bmob.data.entity.User

class AppViewModel : ViewModel() {

    /** 当前登录用户 */
    var user = MutableLiveData<User>()

    /** 当前登录用户的身份 */
    var userIdentification = MutableLiveData<Int>()

    fun setUserIdentification(identification: Int) {
        userIdentification.value = identification
    }

    fun setUser(user: User) {
        this.user.value = user
    }
}