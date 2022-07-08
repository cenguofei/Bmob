package com.example.bmob.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bmob.R
import com.example.bmob.data.entity.*

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


    @JvmStatic
    @BindingAdapter("loadRoundCircleHeadImage")
    fun loadRoundCircleHeadImage(imageView: ImageView?, url: String?){
        if (imageView != null) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.default_head)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(18)))
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("loadHeadImage")
    fun loadHeadImage(imageView: ImageView?, url: String?){
        if (imageView != null) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.default_head)
//                .apply(RequestOptions.bitmapTransform(RoundedCorners(25)))
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("loadUserCircleImage")
    fun loadUserCircleImage(imageView: ImageView?, url: String?){
        if (imageView != null) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.default_head)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("loadRectImage")
    fun loadRectImage(imageView: ImageView?, url: String?){
        if (imageView != null) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.default_background)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("setRoleStartHello")
    fun setRoleStartHello(textView: TextView?,user: User?){
        val identity = when (user?.identification) {
            IDENTIFICATION_STUDENT -> {
                "学生"
            }
            IDENTIFICATION_TEACHER -> {
                "老师"
            }
            IDENTIFICATION_DEAN -> {
                "系主任"
            }
            else -> {
                "教务长"
            }
        }
        val text = "你好，${user?.school}的$identity，欢迎来到Bmob"
        if (textView != null) {
            textView.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("loadUserCircleImage1")
    fun loadUserCircleImage1(imageView: ImageView?, url: String?){
        if (imageView != null) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.default_head)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(150)))
                .into(imageView)
        }
    }


    @JvmStatic
    @BindingAdapter("teacherThesisIsApproved")
    fun teacherThesisIsApproved(textView: TextView?,thesis: Thesis){
        when(thesis.thesisState){
            NOT_APPROVED -> {
                textView?.text = "审核中"
            }
            ALREADY_APPROVED -> {
                textView?.text = "审核通过"
            }
            THESIS_APPROVED_REJECTED -> {
                textView?.text = "审核未通过"
            }
        }
    }
}