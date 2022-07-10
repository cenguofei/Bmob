
import android.os.Environment
import android.util.Log
import com.example.bmob.utils.LOG_TAG
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class PhoneBillExpressBean(
    var userData:String,
    val senderName:Int,
    val senderPhoneNumber:String,
    val senderState:String,
    val senderDetailed:String,
    val senderAddressName:String,
    val addresseeName:String,
    val addresseeState:String,
    val addresseeDetailed:String,
    val price:String,
    val commission:String,
    val createAt:String
)

/**
 * 导出Excel
 * @param listData
 * @return
 */
fun exportExcel(listData: List<PhoneBillExpressBean>): Boolean {
    return try {
        // 创建excel xlsx格式
        val wb: Workbook = XSSFWorkbook()
        // 创建工作表
        val sheet: Sheet = wb.createSheet()
        val title = arrayOf(
            "用户",
            "寄件人姓名",
            "寄件人手机号",
            "寄件人省/市/区",
            "寄件人详细地址",
            "收件人姓名",
            "收件人手机号",
            "收件人省/市/区",
            "收件人详细地址",
            "实付款",
            "佣金",
            "创建时间"
        )
        //创建行对象
        var row: Row = sheet.createRow(0)
        // 设置有效数据的行数和列数
        val colNum =
            title.size // {"用户", "寄件人姓名", "寄件人手机号", "寄件人省/市/区", "寄件人详细地址", "收件人姓名", "收件人手机号", "收件人省/市/区", "收件人详细地址","实付款", "佣金","创建时间"}
        for (i in 0 until colNum) {
            sheet.setColumnWidth(i, 20 * 256) // 显示20个字符的宽度
            val cell1: Cell = row.createCell(i)
            //第一行
            cell1.setCellValue(title[i])
        }
        Log.v(LOG_TAG,"表头创建成功：$row")

        // 导入数据
        for (rowNum in listData.indices) {

            // 之所以rowNum + 1 是因为要设置第二行单元格
            row = sheet.createRow(rowNum + 1)
            // 设置单元格显示宽度
            row.heightInPoints = 28f

            val bean: PhoneBillExpressBean = listData[rowNum]
            for (j in title.indices) {
                val cell: Cell = row.createCell(j)
                when (j) {
                    0 ->                             //用户
                        cell.setCellValue(bean.userData)
                    1 ->                             //寄件人姓名
                        cell.setCellValue(bean.addresseeDetailed)
                    2 ->                             //寄件人手机号
                        cell.setCellValue(bean.addresseeDetailed)
                    3 ->                             //寄件人省/市/区
                        cell.setCellValue(bean.userData)
                    4 ->                             //寄件人详细地址
                        cell.setCellValue(bean.commission)
                    5 ->                             //收件人姓名
                        cell.setCellValue(bean.price)
                    6 ->                             //收件人手机号
                        cell.setCellValue(bean.senderDetailed)
                    7 ->                             //收件人省/市/区
                        cell.setCellValue(bean.senderPhoneNumber)
                    8 ->                             //收件人详细地址
                        cell.setCellValue(bean.senderState)
                    9 ->                             //实付款
                        cell.setCellValue(bean.price + "元")
                    10 ->                             //佣金
                        cell.setCellValue(bean.commission + "元")
                    11 ->                             //创建时间
                        cell.setCellValue(bean.createAt)
                }
            }
            Log.v(LOG_TAG,"创建第 $rowNum 行")
        }
        val mSDCardFolderPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        Log.v(LOG_TAG,"mSDCardFolderPath:$mSDCardFolderPath")
        val dir = File(mSDCardFolderPath)


        //判断文件是否存在
//        if (!dir.isFile) {
//            //不存在则创建
//            Log.v(LOG_TAG,"不存在文件，开始创建")
//            dir.mkdir()
//            Log.v(LOG_TAG,dir.path)
//            Log.v(LOG_TAG,dir.absolutePath)
////            Log.v(LOG_TAG, dir.createNewFile().toString())
//        }else{
//            Log.v(LOG_TAG,"文件存在")
//        }
        //            File excel=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), convertTime(System.currentTimeMillis(),"MM月dd日HH时mm分")+".xlsx");
        val excel =
            File(dir, convertTime(System.currentTimeMillis(), "MM月dd日HH时mm分").toString() + ".xlsx")
        Log.v(LOG_TAG,"filename:${excel.name}")
        val fos = FileOutputStream(excel)
        wb.write(fos)

        fos.flush()
        fos.close()
        true
    } catch (e: IOException) {
        Log.e(LOG_TAG, "异常 exportExcel", e)
        false
    }
}

//时间戳转换字符串
fun convertTime(time: Long, patter: String?): String? {
    val sdf = SimpleDateFormat(patter)
    return sdf.format(Date(time))
}



