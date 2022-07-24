package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject

/**
 * 注册页面使用
 * 用来查询学校 系  院是否存在
 */
data class School(
    val schoolName: String, //校名
    val departments: List<String>, //这个学校的系
    val college: List<String>  //系下的学院
) : BmobObject()