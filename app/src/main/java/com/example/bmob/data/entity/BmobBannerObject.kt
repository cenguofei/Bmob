package com.example.bmob.data.entity

import cn.bmob.v3.BmobObject

/**
 * 学生首页banner的数据对象
 * 根据使用情况再对属性进行修改
 */
data class BmobBannerObject(
    val picUrl: String,  //图片url
    val likes: Int,  //有多少人对这个图片的内容感兴趣或喜欢
    val source: String? = null //来源
) : BmobObject()