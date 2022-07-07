package com.example.bmob.fragments.student.file
import android.os.Environment
import cn.bmob.v3.Bmob.getFilesDir
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.File

class File {

    fun writeExcel(addressesFormat:Array<Array<Array<String>>>,path:String) {
        //创建Excel
        val wb = XSSFWorkbook()

        //创建工作表
        val roomSheet = wb.createSheet("户室数据")
        val unitSheet = wb.createSheet("单元数据")
        val floorSheet = wb.createSheet("楼层数据")
        val buildingSheet = wb.createSheet("幢楼数据")
        val doorplateSheet = wb.createSheet("门牌数据")
        val otherSheet = wb.createSheet("其他数据")

        //创建表头
        var row = roomSheet.createRow(0)
        row.createCell(0).setCellValue("地址名称")
        row.createCell(1).setCellValue("行政区划中文")
        row.createCell(2).setCellValue("街路巷中文")
        row.createCell(3).setCellValue("门牌号")
        row.createCell(4).setCellValue("门牌后缀中文")
        row.createCell(5).setCellValue("幢楼号")
        row.createCell(6).setCellValue("幢楼后缀中文")
        row.createCell(7).setCellValue("单元号")
        row.createCell(8).setCellValue("室号")
        row.createCell(9).setCellValue("室号后缀中文")
        row.createCell(10).setCellValue("地址类型")

        row = unitSheet.createRow(0)
        row.createCell(0).setCellValue("地址名称")
        row.createCell(1).setCellValue("行政区划中文")
        row.createCell(2).setCellValue("街路巷中文")
        row.createCell(3).setCellValue("门牌号")
        row.createCell(4).setCellValue("门牌后缀中文")
        row.createCell(5).setCellValue("幢楼号")
        row.createCell(6).setCellValue("幢楼后缀中文")
        row.createCell(7).setCellValue("单元号")
        row.createCell(8).setCellValue("地址类型")

        row = floorSheet.createRow(0)
        row.createCell(0).setCellValue("地址名称")
        row.createCell(1).setCellValue("行政区划中文")
        row.createCell(2).setCellValue("街路巷中文")
        row.createCell(3).setCellValue("门牌号")
        row.createCell(4).setCellValue("门牌后缀中文")
        row.createCell(5).setCellValue("幢楼号")
        row.createCell(6).setCellValue("幢楼后缀中文")
        row.createCell(7).setCellValue("楼层号")
        row.createCell(8).setCellValue("地址类型")

        row = buildingSheet.createRow(0)
        row.createCell(0).setCellValue("地址名称")
        row.createCell(1).setCellValue("行政区划中文")
        row.createCell(2).setCellValue("街路巷中文")
        row.createCell(3).setCellValue("门牌号")
        row.createCell(4).setCellValue("门牌后缀中文")
        row.createCell(5).setCellValue("幢楼号")
        row.createCell(6).setCellValue("幢楼后缀中文")
        row.createCell(7).setCellValue("地址类型")

        row = doorplateSheet.createRow(0)
        row.createCell(0).setCellValue("地址名称")
        row.createCell(1).setCellValue("行政区划中文")
        row.createCell(2).setCellValue("街路巷中文")
        row.createCell(3).setCellValue("门牌号")
        row.createCell(4).setCellValue("门牌后缀中文")
        row.createCell(5).setCellValue("地址类型")

        row = otherSheet.createRow(0)
        row.createCell(0).setCellValue("地址名称")
        row.createCell(1).setCellValue("行政区划中文")
        row.createCell(2).setCellValue("街路巷中文")
        row.createCell(3).setCellValue("地址类型")

        //下面是此项目的业务需要，主要是遍历创建单元格
        for (i in addressesFormat[0].indices) {
            val newRow = roomSheet.createRow(i+1)
            for (j in addressesFormat[0][i].indices){
                newRow.createCell(j).setCellValue(addressesFormat[0][i][j])
            }
        }
        for (i in addressesFormat[1].indices) {
            val newRow = unitSheet.createRow(i+1)
            var n:Int = -1
            for (j in addressesFormat[1][i].indices){
                if(j!=8&&j!=9){
                    newRow.createCell(++n).setCellValue(addressesFormat[1][i][j])
                }
            }
        }
        for (i in addressesFormat[2].indices) {
            val newRow = floorSheet.createRow(i+1)
            var n:Int = -1
            for (j in addressesFormat[2][i].indices){
                if(j!=8&&j!=9){
                    newRow.createCell(++n).setCellValue(addressesFormat[2][i][j])
                }
            }
        }
        for (i in addressesFormat[3].indices) {
            val newRow = buildingSheet.createRow(i+1)
            var n:Int = -1
            for (j in addressesFormat[3][i].indices){
                if(j!=7&&j!=8&&j!=9){
                    newRow.createCell(++n).setCellValue(addressesFormat[3][i][j])
                }
            }
        }
        for (i in addressesFormat[4].indices) {
            val newRow = doorplateSheet.createRow(i+1)
            var n:Int = -1
            for (j in addressesFormat[4][i].indices){
                if(j!=5&&j!=6&&j!=7&&j!=8&&j!=9){
                    newRow.createCell(++n).setCellValue(addressesFormat[4][i][j])
                }
            }
        }
        for (i in addressesFormat[5].indices) {
            val newRow = otherSheet.createRow(i+1)
            var n:Int = -1
            for (j in addressesFormat[5][i].indices){
                if(j!=5&&j!=6&&j!=7&&j!=8&&j!=9){
                    newRow.createCell(++n).setCellValue(addressesFormat[5][i][j])
                }
            }
        }
//        写入文件路径
//        val filePath = Environment.getExternalStorageDirectory()
//        val filePath = getFilesDir().getAbsolutePath() + "/DeviceMsg"

        val fileOut = FileOutputStream(File(path))


        // 将workbook写到输出流中
        wb.write(fileOut)
        fileOut.flush()
        fileOut.close()
        wb.close()
    }



}