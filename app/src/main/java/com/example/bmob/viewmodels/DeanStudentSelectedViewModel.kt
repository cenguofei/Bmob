package com.example.bmob.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class DeanStudentSelectedViewModel(handler:SavedStateHandle):ViewModel() {

    /**
     * 查询已经选择课题的本系的学生
     *
     * 查询没有选择课题的本系的学生
     *
     *
     * 1. 导出选题结果
     * 系主任通过
     *      Thesis的属性：学校school  院college  系department  找到系里面的老师
     *              需要传入系主任的信息，即
     *              select * from Thesis
     *                      where Thesis.school = dean.school
     *                       and Thesis.college = dean.college
     *                       and Thesis.department = dean.department
     *
     *
     * 然后通过老师的objectId(系统属性) 找到属于这个老师的所有Thesis
     *
     * 最后，得到 studentsList
     *
     * 2. 查看未选学生名单
     * 系主任通过 学校school  院college  系department 找到该系里面所有学生的名单
     *
     * 然后根据User.studentSelectState这个属性筛选出未选学生名单
     *
     * 然后按照班级分类
     */

}