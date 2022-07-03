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
    val identification:Int, //身份
    var name:String?=null,  //姓名
    /**
     * 下面三个属性在注册页面的时候就确定了，
     * 只要用户现在有账号，他就有下面的属性，并且不为空
     *
     * 用户也可以修改这些属性
     */
    val school:String?=null, //学校
    val department:String?=null,  //系
    val college:String?=null  //学院
):BmobUser()

//identification取值
const val IDENTIFICATION_STUDENT = 1 //身份为学生
const val IDENTIFICATION_TEACHER = 2 //身份为老师
const val IDENTIFICATION_DEAN = 3 //身份为系主任
const val IDENTIFICATION_PROVOST = 4 //身份为教务长
const val USER_HAS_NOT_IDENTIFICATION = -1  //不写入云数据库，只在本地dataStore里面使用

