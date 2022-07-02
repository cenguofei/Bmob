package com.example.bmob.data.entity

import cn.bmob.v3.BmobUser

data class User(
    val nickname:String? = null,  //昵称
    val avatarUrl:String? = null,  //头像url
    val backgroundUrl:String? = null,  //背景url
    val age:Int? = null,    //年龄
    val gender:Int? = null,  //性别
    val birth:String? = null, //生日
    val address:String? = null, //地址
    val identification:Int? = null
):BmobUser()

//identification取值
const val IDENTIFICATION_STUDENT = 1 //身份为学生
const val IDENTIFICATION_TEACHER = 2 //身份为老师
const val IDENTIFICATION_DEAN = 3 //身份为系主任
const val IDENTIFICATION_PROVOST = 4 //身份为教务长

