package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject
import java.util.*

data class ProvostReleaseSelectTimeEntity(
    val school:String,
    val beginTime:Date,
    val endTime:Date
):BmobObject()