package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.EMPTY_TEXT
import com.example.bmob.databinding.FragmentTeacherNewThesisBinding

class TeacherThesisViewModel:ViewModel() {
    private

    /**
     * 查询该教师的所有课题
     */
//    private fun queryTeacherAllThesis(){
//        BmobQuery<Thesis>()
//            .findObjects(object :FindListener<Thesis>(){
//                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
//
//                }
//            })
//    }
    fun getUserInfo(callback: (isSuccess: Boolean, user: User?) -> Unit) {

    }

    /**
     * 保存课题到数据库
     */
    fun saveThesis(thesis: Thesis,callback:(isSuccess:Boolean,msg:String)->Unit){
        thesis.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 == null){
                    callback.invoke(true, EMPTY_TEXT)
                }else{
                    callback.invoke(false,p1.message.toString())
                }
            }
        })
    }

    /**
     * 输入合法才保存
     */
    fun isInputValid(binding: FragmentTeacherNewThesisBinding):Boolean{
        return binding.thesisTitle.text.isNotEmpty()
                && binding.thesisField.text.isNotEmpty()
                && binding.thesisBrief.text.isNotEmpty()
                && binding.thesisRequire.text.isNotEmpty()
    }
}