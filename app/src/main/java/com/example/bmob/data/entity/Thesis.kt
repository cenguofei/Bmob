package com.example.bmob.data.entity

import android.os.Parcelable
import cn.bmob.v3.BmobObject
import kotlinx.parcelize.Parcelize

@Parcelize
data class Thesis(
    var title: String = "", //标题
    var description: String = "", //论文描述
    var field: String = "", //所属领域

    var teacherId: String = "", //创造该选题的教师id
    var teacherAvatarUrl: String = "", //教师的头像地址
    var teacherName: String = "", //创造该选题的教师
    /**
     * 老师的特有功能->导出选题学生名单(包括课题名称，学生姓名，班级)
     *
     * 导出选择该课题的学生信息
     *
     * 系主任查询到该课题时，需要判断studentList
     * @see studentsList
     * @see teaIsSeOne
     * 当老师决定哪个学生选择该课题后更新 teacherIsSelectOneStudent
     *      false代表老师还没有决定哪个学生选
     *      true代表老师决定某个学生选择了该课题
     */
    var studentsList: MutableList<User>? = null, //选择该论文的学生
    var teaIsSeOne: Boolean = false,

    var selectState: Int = SELECT_STATE_UNSELECTED, //论文是否被选，0未选，1已选

    var selectedNum: Int = 0, //被学生选择的次数

    var school: String = "",
    var department: String = "",
    var college: String = "",
    var userDetail: String = "",
    var require: String = "",//论文要求

    /**
     * 针对学生 ，论文是否可选，当审核通过并且选题时间开始后为true，表示可选
     *  false 初始值
     *  true  系主任审批通过
     */
    var enabledToStudent: Boolean = false,

    //针对老师,审批状态
    var thesisState: Int = NOT_APPROVED

    //针对系主任
    //看整个系/专业每个老师  每个课题的选择人数
    //老师->Thesis, selectedNum
    /**
     * 1. 导出选题结果
     * 系主任通过 学校school  院college  系department  找到系里面的老师
     *
     * 然后通过老师的objectId(系统属性) 找到属于这个老师的所有Thesis
     *
     * 最后，得到 studentsList
     *
     * 2. 查看未选学生名单
     * 系主任通过 学校school  院college  系department 找到该系里面所有学生的名单
     *
     * 然后根据studentSelectState这个属性筛选出未选学生名单
     *
     * 然后按照班级分类
     */

    //教务长只有开设选题和结束选题事件的功能
) : BmobObject(), Parcelable

//selectState 论文是否被选，0未选，1已选
const val SELECT_STATE_SELECTED = 1
const val SELECT_STATE_UNSELECTED = 0

//针对老师,审批状态 thesisState:Int
// 针对老师 课题状态 Not approved
//这三个状态都不在选课时间段
const val NOT_APPROVED = 0  //表示系主任还没审批课题
const val ALREADY_APPROVED = 1  //审批通过
const val THESIS_APPROVE_REJECTED = 2 //审批未通过