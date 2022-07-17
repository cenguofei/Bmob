package com.example.bmob

import com.alibaba.excel.EasyExcel
import com.example.bmob.data.entity.User
import com.example.bmob.utils.EasyExcelUtil.getUserData
import com.example.bmob.utils.UserTest
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun writeExcelTest() {
        val filename = "C:\\Users\\26927\\Desktop\\tt\\user1.xlsx"

        EasyExcel.write(filename, UserTest::class.java)
            .sheet("用户信息")
            .doWrite(com.example.bmob.utils.EasyExcelUtil.getUserData())
    }

    fun test1(){
//        String filename = "D:\\study\\excel\\user2.xlsx";
//        // 创建ExcelWriter对象
//        ExcelWriter excelWriter = EasyExcel.write(filename, User.class).build();
//        // 创建Sheet对象
//        WriteSheet writeSheet = EasyExcel.writerSheet("用户信息").build();
//        // 向Excel中写入数据
//        excelWriter.write(getUserData(), writeSheet);
//        // 关闭流
//        excelWriter.finish();


    }

    @Test
    fun testWriteExcel2() {
        val filename = "C:\\Users\\26927\\Desktop\\user2.xlsx"
        // 创建ExcelWriter对象
        val excelWriter = EasyExcel.write(filename, User::class.java).build()
        // 创建Sheet对象
        val writeSheet = EasyExcel.writerSheet("用户信息").build()
        // 向Excel中写入数据
        excelWriter.write(getUserData(), writeSheet)
        // 关闭流
        excelWriter.finish()
    }
}

