package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject


/**
 * 对用户留言
 *
 * @see createdAt
 * 留言创建的时间
 *
 */
data class Message(
    val fromUser:User,  //谁留的
    val toUser:User,    //留给谁

    val content:String,  //留言的内容

):BmobObject()
