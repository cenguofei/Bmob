package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject

data class ReleaseTime(
    val provostObjectId:String,
    val provostName:String,
    val school:String,
    var beginTime:String,
    var endTime:String,
    val isIssued:Boolean
):BmobObject()