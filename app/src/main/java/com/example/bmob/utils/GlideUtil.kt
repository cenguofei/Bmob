package com.example.bmob.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.bmob.R

object GlideUtil {
    @JvmStatic
    @BindingAdapter("android:loadHeadImageWithUrl")
    fun loadHeadImageWithUrl(imageView: ImageView,uri: Uri){
        Glide.with(imageView.context)
            .load(uri)
            .placeholder(R.drawable.ic_launcher_background)
            .into(imageView)
    }
}