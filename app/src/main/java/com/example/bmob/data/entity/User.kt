package com.example.bmob.data.entity

import android.os.Parcelable
import cn.bmob.v3.BmobUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    //1. 公有属性
    var nickname: String? = null,  //昵称
    var avatarUrl: String? = null,  //头像url
    var backgroundUrl: String? = null,  //背景url
    var age: Int? = null,    //年龄

    var gender: String? = null,  //性别
    var birth: String? = null, //生日

    var address: String? = null, //地址
    val identification: Int, //身份

    var name: String? = null,  //姓名
    var signature: String? = null,  //用户签名

    /**
     * 下面三个属性在注册页面的时候就确定了，
     * 只要用户现在有账号，他就有下面的属性，并且不为空
     *
     * 用户也可以修改这些属性
     *
     * 通过这三个属性可以把老师和学生联系起来
     */
    var school: String? = null, //学校

    var department: String? = null,  //系
    var college: String? = null,  //学院

    //2. 特有属性
    //1) 学生
    /**
     * 当学生选择课题时，标识学生是否已经成功选择该课题
     *
     * 所谓成功，就是 开放时间后->教师决定->最终是否被留下
     */
    val studentClass: String? = null, //学生所属班级
    var studentThesis: Thesis? = null,

    /**
     * @see studentSelectState
     * true已选，false未选
     */
    var studentSelectState: Boolean? = null,
    var isAgree: Boolean? = null,

    //2) 老师
    val teacherDetail: String? = null, //老师的描述，查看课题详情的时候显示，这个是系统自动为老师生成的资料
) : BmobUser(), Parcelable

//identification取值
inline val IDENTIFICATION_STUDENT: Int  //身份为学生
    get() = 1
inline val IDENTIFICATION_TEACHER: Int  //身份为老师
    get() = 2
inline val IDENTIFICATION_DEAN: Int  //身份为系主任
    get() = 3
inline val IDENTIFICATION_PROVOST: Int   //身份为教务长
    get() = 4
inline val USER_HAS_NOT_IDENTIFICATION: Int  //不写入云数据库，只在本地dataStore里面使用
    get() = -1


//针对学生 selectThesisState取值
inline val STUDENT_NOT_SELECT_THESIS: Boolean
    get() = false
inline val STUDENT_HAS_SELECTED_THESIS: Boolean
    get() = true
