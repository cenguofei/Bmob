package com.example.bmob.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.entity.School
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobRepository

class StudentSelectViewModel:ViewModel() {
    private val repository = BmobRepository.getInstance()
    private var mutableThesisLiveData = MutableLiveData<MutableList<Thesis>>()

    fun getMutableThesisLiveData() = mutableThesisLiveData

    /**
     * 学生选择自己所在系里面的所有课题
     */
    fun findAllTeacherInDepartment(callback:(
        isSuccess:Boolean,teacherList:MutableList<User>?,msg:String)->Unit){
        getUserInfo{isSuccess, user ->
            if (isSuccess){
                //三个条件
                val equalToSchool = BmobQuery<User>()
                    .addWhereEqualTo("school", user!!.school)
                val equalToDepartment = BmobQuery<User>()
                    .addWhereEqualTo("department",user.department)
                val equalToCollege = BmobQuery<User>()
                    .addWhereEqualTo("college",user.college)
                val equalToIdentification = BmobQuery<User>()
                    .addWhereEqualTo("identification", IDENTIFICATION_TEACHER)

                val queryList = ArrayList<BmobQuery<User>>().run {
                    add(equalToSchool)
                    add(equalToDepartment)
                    add(equalToCollege)
                    add(equalToIdentification)
                    this@run
                }

                BmobQuery<User>()
                    .and(queryList)
                    .findObjects(object :FindListener<User>(){
                        override fun done(p0: MutableList<User>?, p1: BmobException?) {
                            if (p1 == null){
                                if (p0 == null){
                                    callback.invoke(true,null, EMPTY_SEARCH_RESULT)
                                }else{
                                    callback.invoke(true,p0, EMPTY_MESSAGE)
                                }
                            }else{
                                callback.invoke(false,null,p1.message.toString())
                            }
                        }
                    })
            }else{
                callback.invoke(false,null,"查询不到用户信息")
            }
        }
    }

    private fun getUserInfo(callback:(isSuccess:Boolean,user: User?)->Unit){
        repository.getUserInfo(callback)
    }


    /**
     * 查询SelectFragment中args.user
     * 对应的所有课题
     *
     * select * from Thesis
     *      where user.objectId = Thesis.teacherId
     */
    fun getTeacherAllThesis(thesisUser: User,callback:(message:String)->Unit){
        BmobQuery<Thesis>()
            .addWhereEqualTo("teacherId",thesisUser.objectId)
            .findObjects(object :FindListener<Thesis>(){
                override fun done(p0: MutableList<Thesis>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 == null){
                            callback.invoke("没有搜索到该教师的课题")
                        }else{
                            mutableThesisLiveData.value = p0
                        }
                    }else{
                        callback.invoke("出错了：${p1.message}")
                    }
                }
            })
    }

    fun addStudentToThesis(thesisObjectId:String,studentsList:List<User>,
                           callback: (
                               isSuccess: Boolean,
                               msg: String
                           ) -> Unit)
    {
        val user = User(
            "昵称",
            "https://bmob-cdn-30807.bmobpay.com/2022/07/04/8d2f8b9c40202dd080648e733fa1775c.jpg",
            "https://bmob-cdn-30807.bmobpay.com/2022/07/04/8d2f8b9c40202dd080648e733fa1775c.jpg",
            19,
            "男",
            "1999-11-15",
            "云南",
            2,
            "张三",
            "明天会更好",
            "北京大学",
            "计算机系",
            "xxx院",
            "studentClass",
            false,
            "教师详情"
        )
        Thesis(studentsList = listOf(user,user,user))
            .update("sTZWFFFO",object :UpdateListener(){
                override fun done(p0: BmobException?) {
                    if (p0 == null){
                        callback.invoke(true, EMPTY_MESSAGE)
                    }else callback.invoke(false,p0.message.toString())
                }
            })
    }
}

const val EMPTY_MESSAGE = ""
const val EMPTY_SEARCH_RESULT = ""