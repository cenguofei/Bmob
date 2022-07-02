package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject

data class Thesis(
    val classIds:List<String>,  //班级id，该论文属于哪些班级选
    val version:Int,  //论文版本
    val title:String, //标题
    val content:String, //内容
    val description:String, //论文描述
    val fields:List<String>, //所属领域
    val teacherId:String, //创造该选题的教师id
    val teacherName:String, //创造该选题的教师
    val studentsId:List<String>, //选择该论文的学生
    val level:Int,  //难度 1简单，2中等，3困难
    val enabled:Boolean, //论文是否可选，当审核通过并且选题时间开始后为true，表示可选
    val state:Int, //论文是否备选，0未选，1已选
    val selectedNum:Int //被学生选择的次数
):BmobObject()
