package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel

class EditThesisViewModel:ViewModel() {


}

//测试
//        binding.textView4.setOnClickListener {
//            model.addThesis(
//                Thesis(
//                    listOf("1", "2"), 1, "title",
//                    "内容", "描述", "方向为Android", "教师id", "教师名字",
//                    listOf("学生1 id", "学生2 id"), 2, false, 1, 99, "西南大学",
//                    "计算机系", "商贸学院"
//                )
//            ) { isSuccess, objectId, msg ->
//                if (isSuccess) {
//                    Log.v(LOG_TAG, "论文添加成功,id为：$objectId")
//                } else {
//                    Log.v(LOG_TAG, "论文添加失败：$msg")
//                }
//            }
//            BmobRepository.getInstance().searchAnyThesis("title") { isSuccess, thesis, msg ->
//                Log.v(
//                    LOG_TAG,
//                    "size:${thesis?.size},isSuccess:$isSuccess thesis:$thesis msg:$msg\n"
//                )
//            }
//        }