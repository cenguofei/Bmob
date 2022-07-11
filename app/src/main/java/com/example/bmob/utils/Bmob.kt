package com.example.bmob.utils

import android.content.Context
import android.widget.Toast

fun showMsg(context: Context, msg:String){
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
}

inline val EMPTY_TEXT:String
    get() = ""

inline val School: String
    get() = "school"

inline val College:String
    get() = "college"

inline val Department:String
    get() = "department"

inline val ThesisState:String
    get() = "thesisState"

inline val Username:String
    get() = "username"

inline val StudentSelectState:String
    get() ="studentSelectState"

inline val Identification:String
    get() = "identification"

inline val ObjectId:String
    get() = "objectId"

inline val TeacherId:String
    get() = "teacherId"

inline val ToUserId:String
    get() = "toUserId"

inline val FromUserId:String
    get() = "fromUserId"

inline val ForThesis:String
    get() = "forThesis"
