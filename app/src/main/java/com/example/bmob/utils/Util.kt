package com.example.bmob.utils

import android.content.Context
import android.widget.Toast

fun showMsg(context: Context, msg:String){
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
}