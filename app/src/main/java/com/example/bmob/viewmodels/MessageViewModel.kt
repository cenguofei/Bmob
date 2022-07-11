package com.example.bmob.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.example.bmob.data.entity.Message
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.utils.EMPTY_TEXT
import com.example.bmob.utils.ForThesis
import com.example.bmob.utils.FromUserId
import com.example.bmob.utils.ToUserId

class MessageViewModel(private val handler:SavedStateHandle):ViewModel() {

    /**
     * 上传留言
     */
    fun uploadMessage(
        forThesis: Thesis,
        content:String,
        fromUser:User,
        toUserId: String,
        callback:(isSuccess:Boolean,message:String)->Unit
    ){
        Message(forThesis,fromUser.objectId,toUserId,content,fromUser.avatarUrl!!,fromUser.name!!)
            .save(object :SaveListener<String>(){
                override fun done(p0: String?, p1: BmobException?) {
                    if (p1 == null){
                        callback.invoke(true,"留言成功")
                    }else{
                        callback.invoke(false,"留言失败")
                    }
                }
            })
    }


    /**
     * 保存用户的留言
     */
    fun addUserLeaveMessage(message: Message){
        if(!handler.contains(LEAVE_MESSAGE_KEY)){
            handler.set(LEAVE_MESSAGE_KEY, mutableListOf(message))
        }else{
            val mutableList = handler.getLiveData<MutableList<Message>>(LEAVE_MESSAGE_KEY).value
            mutableList?.add(message)
            handler.set(LEAVE_MESSAGE_KEY,mutableList)
        }
    }

    /**
     * 获取用户以前的留言，
     * 以及当前登录后的留言，登录后的留言暂时保存到了handler
     */
//    fun getUserMessageLiveData(
//        loginTeacher: User,
//        callback: (message: String) -> Unit
//    ): MutableLiveData<MutableList<Message>> {
//        if (!handler.contains(LEAVE_MESSAGE_KEY)){
//            getRemoteHistoryMessage(loginTeacher){isSuccess, mutableListMessage, msg ->
//                if (isSuccess){
//                    handler.set(LEAVE_MESSAGE_KEY,mutableListMessage)
//                }else callback.invoke(msg)
//            }
//        }
//        return handler.getLiveData(LEAVE_MESSAGE_KEY)
//    }

    /**
     * 远程获取历史留言
     * @param loginTeacher
     * 当前登录的教师用户
     *
     * 获取教师给其他课题的留言  或者 其他人给教师的留言
     */
    fun getTeacherRemoteHistoryMessage(
        loginTeacher: User,
        callback: (isSuccess: Boolean,mutableListMessage:MutableList<Message>?, msg: String) -> Unit
    ){
        //别人给当前教师的留言
        val addWhereEqualTo1 = BmobQuery<Message>().addWhereEqualTo(ToUserId, loginTeacher.objectId)
        //当前教师给别人的留言
        val addWhereEqualTo2 =
            BmobQuery<Message>().addWhereEqualTo(FromUserId, loginTeacher.objectId)

        val queryList = ArrayList<BmobQuery<Message>>().apply {
            add(addWhereEqualTo1)
            add(addWhereEqualTo2)
        }

        BmobQuery<Message>()
            .or(queryList)
            .include(ForThesis) //如果有关联关系，一定要添加include
            .findObjects(object : FindListener<Message>(){
                override fun done(p0: MutableList<Message>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            callback.invoke(true,p0, EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null,"没有搜索到历史留言")
                        }
                    }else{
                        callback.invoke(false,null,"出错了:${p1.message}")
                    }
                }
            })
    }

    /**
     * 非教师用户给课题的历史留言记录
     */
    fun getRemoteHistoryMessage(
        user: User,
        callback: (isSuccess: Boolean,mutableListMessage:MutableList<Message>?, msg: String) -> Unit
    ){
        BmobQuery<Message>()
            .addWhereEqualTo(FromUserId, user.objectId)  //自己给教师的留言
            .include(ForThesis) //如果有关联关系，一定要添加include
            .findObjects(object : FindListener<Message>(){
                override fun done(p0: MutableList<Message>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            callback.invoke(true,p0, EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null,"没有搜索到历史留言")
                        }
                    }else{
                        callback.invoke(false,null,"出错了:${p1.message}")
                    }
                }
            })
    }


    companion object{
        private const val LEAVE_MESSAGE_KEY = "_leave_message_"
    }

}