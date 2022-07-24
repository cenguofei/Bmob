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
    val forThesis: Thesis, //留言的课题
    val fromUserId: String,  //谁留的
    val toUserId: String,    //留给谁
    val content: String,  //留言的内容
    val fUAvatar: String, //留言者的头像url
    val fUName: String //留言者的名字
) : BmobObject() {
    constructor() : this(
        Thesis(), "", "", "", "", ""
    )
}