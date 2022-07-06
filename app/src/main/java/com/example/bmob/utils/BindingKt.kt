package com.example.bmob.utils

import androidx.databinding.InverseMethod

@InverseMethod("intToString")
fun stringToInt(value:String):Int{
    return try {
        Integer.parseInt(value)
    }catch (e:Exception){
        -1
    }
}

fun intToString(value:Int):String{
    return value.toString()
}




