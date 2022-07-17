package com.example.bmob.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

object JxlExcelUtil {
    private var aria14font: WritableFont? = null
    private var aria10font: WritableFont? = null
    private var aria12font: WritableFont? = null
    private var arial14format: WritableCellFormat? = null
    private var arial10format: WritableCellFormat? = null
    private var arial12format: WritableCellFormat? = null
    private const val UTF8_ENCODING = "UTF-8"

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等..
     */
    private fun format() {
        try {
            aria14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            aria14font!!.colour = jxl.format.Colour.LIGHT_BLUE
            arial14format = WritableCellFormat(aria14font)
            arial14format!!.alignment = jxl.format.Alignment.CENTRE
            arial14format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)
            arial14format!!.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW)

            aria10font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            aria10font!!.colour = jxl.format.Colour.BLUE
            arial10format = WritableCellFormat(aria10font)
            arial10format!!.alignment = jxl.format.Alignment.CENTRE
            arial10format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)

            aria12font = WritableFont(WritableFont.ARIAL, 10)
            arial12format = WritableCellFormat(aria12font)
            arial12format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)
            arial12format!!.alignment = jxl.format.Alignment.CENTRE
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化Excel
     *
     * @param filePath 导出excel存放的地址（目录）
     * @param colsTitleName 标题栏
     */
    private fun initExcel(
        filePath: String,
        colsTitleName: Array<String>,
        sheetName: String
    ) {
        format()
        var workbook: WritableWorkbook? = null
        try {
            val file = File(filePath)
            if (!file.exists()) {
                file.createNewFile()
            }

            workbook = Workbook.createWorkbook(file)

            //设置表格的名字
            val sheet: WritableSheet = workbook.createSheet(sheetName, 0)

            //创建标题栏
            for ((index, y) in colsTitleName.withIndex()) {
                sheet.addCell(Label(index, 0, y, arial10format))
            }
            //设置行高
            sheet.setRowView(0, 340)
            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (workbook != null) {
                try {
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private fun <T> writeObjListToExcel(
        objList: List<T>,
        filePath: String,
        context: Context,
        createRowCallback: (student: T) -> ArrayList<String>
    ) {
        if (objList.isNotEmpty()) {
            var writeBook: WritableWorkbook? = null
            var inputStream: InputStream? = null
            try {
                val setEncode = WorkbookSettings()
                setEncode.encoding = UTF8_ENCODING
                inputStream = FileInputStream(File(filePath))
                val workbook: Workbook = Workbook.getWorkbook(inputStream)

                writeBook = Workbook.createWorkbook(File(filePath), workbook)
                val sheet: WritableSheet = writeBook.getSheet(0)

                for ((j, createRow) in objList.withIndex()) {
                    val row = createRowCallback.invoke(createRow)
                    for ((i, col) in row.withIndex()) {
                        sheet.addCell(Label(i, j + 1, col, arial12format))
                        if (col.length <= 4) {
                            //设置列宽
                            sheet.setColumnView(i, col.length + 8)
                        } else {
                            sheet.setColumnView(i, col.length + 5)
                        }
                    }
                    //设置行高
                    sheet.setRowView(j + 1, 350)
                }
                writeBook.write()
                Toast.makeText(context, "导出Excel成功:$filePath", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.v("cgf", e.message.toString())
                Toast.makeText(context, "导出Excel失败:$filePath", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                if (writeBook != null) {
                    try {
                        writeBook.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun<T> export(
        data:MutableList<T>,
        activity:Activity,
        context: Context,
        excelFileName:String,
        colsTitleName:Array<String>,
        createRowCallback: (student: T) -> ArrayList<String>
    ){
        if (data.isNotEmpty()){
            var filePath = "${activity.getExternalFilesDir("bmobExcel")?.path}"
            if (checkPermission(context)){
                filePath = Environment.getExternalStorageDirectory().toString()
                Log.v("cgf","外部存储：$filePath")
            }else{
                Log.v("cgf","应用内存储：$filePath")
            }
            val file = File(filePath)
            /**
             * 只有应用内部存储才会用到，应用外部存储又系统目录
             * 不需要创建，当然，也可以在系统目录下创建属于自己的目录
             */
            if (!file.exists()) {
                file.mkdirs()
            }
            filePath = "$filePath/$excelFileName"
            initExcel(filePath, colsTitleName,excelFileName.substring(0,excelFileName.length-5))
            writeObjListToExcel(data, filePath, context,createRowCallback)
        }
    }

    private fun checkPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }
}













