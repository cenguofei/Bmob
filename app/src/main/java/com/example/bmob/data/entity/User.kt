package com.example.bmob.data.entity

import android.os.Parcelable
import cn.bmob.v3.BmobUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    //1. 公有属性
    val nickname:String? = null,  //昵称
    var avatarUrl:String? = null,  //头像url
    var backgroundUrl:String? = null,  //背景url
    var age:Int? = null,    //年龄
    var gender:String? = null,  //性别
    var birth:String? = null, //生日
    var address:String? = null, //地址
    val identification:Int, //身份
    var name:String?=null,  //姓名
    var signature:String? = null,  //用户签名

    /**
     * 下面三个属性在注册页面的时候就确定了，
     * 只要用户现在有账号，他就有下面的属性，并且不为空
     *
     * 用户也可以修改这些属性
     *
     * 通过这三个属性可以把老师和学生联系起来
     */
    var school:String?=null, //学校
    var department:String?=null,  //系
    var college:String?=null,  //学院

    //2. 特有属性
    //1) 学生
    /**
     * 当学生选择课题时，标识学生是否已经成功选择该课题
     *
     * 所谓成功，就是 开放时间后->教师决定->最终是否被留下
     */
    val studentClass:String?=null, //学生所属班级
    var studentThesis:Thesis?=null,

    var title:String?=null,
    /**
     * @see studentSelectState
     */
    var studentSelectState:Boolean? = null, //true已选，false未选
    var field:String? = null,
    var require:String? = null,
    var desc:String?=null,
    var isAgree:Boolean?=null,
    var theTeaDetail:String?=null,
    var theTeaAvaUrl:String?= null,


    //2) 老师
    val teacherDetail:String? = null, //老师的描述，查看课题详情的时候显示，这个是系统自动为老师生成的资料

    //3) 系主任


    //4) 教务长


):BmobUser(), Parcelable

//identification取值
const val IDENTIFICATION_STUDENT = 1 //身份为学生
const val IDENTIFICATION_TEACHER = 2 //身份为老师
const val IDENTIFICATION_DEAN = 3 //身份为系主任
const val IDENTIFICATION_PROVOST = 4 //身份为教务长
const val USER_HAS_NOT_IDENTIFICATION = -1  //不写入云数据库，只在本地dataStore里面使用



//针对学生 selectThesisState取值
const val STUDENT_NOT_SELECT_THESIS = false
const val STUDENT_HAS_SELECTED_THESIS = true
