package com.example.bmoblibrary.ext

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showMsgShort(msg:String){
    context?.let {
        Toast.makeText(it,msg,Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showMsgLong(msg:String){
    context?.let {
        Toast.makeText(it,msg,Toast.LENGTH_LONG).show()
    }
}

fun Activity.showMsgShort(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun Context.showMsgShort(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun Activity.showMsgLong(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
}


fun String.logV(tag: String = LOG_TAG_V) {
    Log.v(tag, this)
}

fun String.logI(tag: String = LOG_TAG_I) {
    Log.v(tag, this)
}

inline val LOG_TAG_I: String
    get() = "cgfi"

inline val LOG_TAG_V: String
    get() = "cgfv"
