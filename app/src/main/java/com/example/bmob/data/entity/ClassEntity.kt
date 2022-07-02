package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject

data class ClassEntity(
    val className:String, //班级名称
    val teacherId:String, //班级所属教师id
    val teacherName:String, //教师名字
    val studentId:String, //学生id
    val studentName:String //学生姓名
):BmobObject()