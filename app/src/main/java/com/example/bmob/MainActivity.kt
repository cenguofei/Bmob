package com.example.bmob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.bmob.v3.Bmob

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Bmob.initialize(this,"6c64658a44bd4a2260c527c3ca385248")
    }
}