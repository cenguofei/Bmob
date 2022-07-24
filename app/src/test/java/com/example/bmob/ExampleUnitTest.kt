package com.example.bmob

import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testBirthFormat() {
        val birthStr = "2022-7-30 19:00:00"

        val birthFormat = birthStr.split(" ")[0]
        println("生日：$birthFormat")
    }
}

