//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.widget.EditText
//
//class EditTextWithText : EditText {
//    private var leadText: String? = null
//
//    constructor(context: Context?) : super(context) {}
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    )
//
//    override fun onDraw(canvas: Canvas) {
//        val paint = Paint()
//        paint.textSize = 56f //自定义字大小
//        paint.color = Color.GRAY //自定义字颜色
//        canvas.drawText("  $leadText", 2f, (height / 2 + 20).toFloat(), paint)
//        val paddingLeft = paint.measureText(leadText).toInt()
//        //设置距离光标距离左侧的距离
//        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
//        super.onDraw(canvas)
//    }
//}