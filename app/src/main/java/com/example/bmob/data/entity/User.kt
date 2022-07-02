package com.example.bmob.data.entity

import cn.bmob.v3.BmobUser

data class User(
    val nickname:String,  //昵称
    val avatarUrl:String,  //头像url
    val backgroundUrl:String,  //背景url
    val age:Int,    //年龄
    val gender:Int,  //性别
    val birth:String, //生日
    val address:String //地址
):BmobUser()

