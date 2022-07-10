package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject

data class Message(
    val thesis: Thesis,
    val teacherObjectId:String,
):BmobObject()