//package com.example.bmob.utils
//
//import android.content.Context
//import android.os.Environment
//import android.util.Log
//import com.alibaba.excel.EasyExcel
//import com.alibaba.excel.annotation.ExcelProperty
//import com.example.bmob.data.entity.User
//import com.example.bmob.fragments.dean.select.SelectedStudent
//import java.io.File
//import java.util.*
//
//
//data class UserTest(
//    @ExcelProperty(value = ["用户编号"])
//    var userId: Int? = null,
//
//    @ExcelProperty(value = ["姓名"])
//    var userName: String? = null,
//
//    @ExcelProperty(value = ["性别"])
//    var gender: String? = null,
//
//    @ExcelProperty(value = ["工资"])
//    var salary: Double? = null,
//
//    @ExcelProperty(value = ["入职时间"])
//    var hireDate: Date? = null // lombok 会生成getter/setter方法
//)
//
//object EasyExcelUtil{
//    // 根据user模板构建数据
//    fun getUserData(): List<UserTest> {
//        val users: MutableList<UserTest> = ArrayList()
//        for (i in 1..10) {
//            val user: UserTest = UserTest().apply {
//                userId = i
//                userName = "admin$i"
//                gender = if (i % 2 == 0) "男" else "女"
//                salary = (i * 100).toDouble()
//                hireDate = Date()
//            }
//            users.add(user)
//        }
//        return users
//    }
//
//    fun writeDeanSelectedStudentsToExcel(
//        context: Context,
//        filename:String,
//        data:MutableList<SelectedStudent>,
//        callback:(message:String)->Unit
//    ){
//        val path = context.getExternalFilesDir("bmobExcel")?.path
//        Log.v(LOG_TAG,"path：$path")
//
//        val file = File(path!!)
//        Log.v(LOG_TAG,"file path：${file.path}")
//
//        var mkDirFlag: Boolean
//        try {
//            mkDirFlag = if (!file.exists()){
//                file.mkdirs()
//            }else true
//        }catch (e:Exception){
//            mkDirFlag = false
//            e.printStackTrace()
//        }
//
//        var createNewFileFlag: Boolean
//
//        val saveFile = File("$path/$filename")
//        if (mkDirFlag){
//            Log.v(LOG_TAG,"saveFile path：${saveFile.path}")
//            try {
//                createNewFileFlag = if (!saveFile.exists()){
//                    saveFile.createNewFile()
//                }else true
//            }catch (e:Exception){
//                createNewFileFlag = false
//                e.printStackTrace()
//            }
//        }else createNewFileFlag = false
//
//        if (mkDirFlag && createNewFileFlag){
//            EasyExcel
//                .write(saveFile.path,SelectedStudent::class.java)
//                .sheet("bmob已选学生名单")
//                .doWrite(data)
//        }else{
//            callback.invoke("创建文件夹或文件失败")
//            Log.v(LOG_TAG,"创建文件夹或文件失败")
//        }
//    }
//}
//
//
//
//
//
