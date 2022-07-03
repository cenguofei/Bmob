package com.example.bmob.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapter {
    /**
     * (textView: TextView,phoneNumber:String)，参数这样写就会报下面的错误
     * Parameter specified as non-null is null: method kotlin.jvm.internal.Intrinsics.checkNotNullParameter, parameter phoneNumber
     *
     * 这是Kotlin安全机制的检测，需要在方法中加入 ？
     * 改成：
     *      (textView: TextView?,phoneNumber:String?)
     */
    @JvmStatic
    @BindingAdapter("phoneNumberForFindPassword")
    fun phoneNumberForFindPassword(textView: TextView?,phoneNumber:String?){
        if (phoneNumber != null) {
            if (phoneNumber.isEmpty()){
                if (textView != null) {
                    textView.text = ""
                }
            }else{
                val startNum = phoneNumber.subSequence(0,3)
                val endNum = phoneNumber.subSequence(phoneNumber.length-3,phoneNumber.length)
                val content = "请填写出完整的手机号 $startNum******$endNum 以验证身份"
                val spannableString = SpannableString(content)
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FF0000")),
                    11,
                    spannableString.length-5,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (textView != null) {
                    textView.text = spannableString
                }
            }
        }
    }
}