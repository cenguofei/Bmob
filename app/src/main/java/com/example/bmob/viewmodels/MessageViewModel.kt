package com.example.bmob.viewmodels

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.Message
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.MessageItemLayoutBinding
import com.example.bmob.utils.*
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import java.util.*

class MessageViewModel(application: Application):AndroidViewModel(application) {

    private val _messagesLiveData = MutableLiveData<MutableList<Message>>()

    /** 1. 上传留言，搜索并显示留言 */
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
     * 远程获取历史留言
     * @param loginTeacher
     * 当前登录的教师用户
     *
     * 获取教师给其他课题的留言  或者 其他人给教师的留言
     */
    fun getTeacherRemoteHistoryMessage(
        loginTeacher: User,
        callback: FetchDataCallback
    ){
        val queryList = ArrayList<BmobQuery<Message>>().apply {
            //别人给当前教师的留言
            add(BmobQuery<Message>().addWhereEqualTo(ToUserId, loginTeacher.objectId))
            //当前教师给别人的留言
            add(BmobQuery<Message>().addWhereEqualTo(FromUserId, loginTeacher.objectId))
        }
        BmobQuery<Message>()
            .or(queryList)
            .include(ForThesis) //如果有关联关系，一定要添加include
            .findObjects(object : FindListener<Message>(){
                override fun done(p0: MutableList<Message>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            _messagesLiveData.value = p0
                        }else{
                            callback.invoke("没有搜索到历史留言")
                        }
                    }else{
                        callback.invoke("出错了:${p1.message}")
                    }
                }
            })
    }

    /**
     * 非教师用户给课题的历史留言记录
     */
    fun getRemoteHistoryMessage(
        user: User,
        callback: FetchDataCallback
    ){
        BmobQuery<Message>()
            .addWhereEqualTo(FromUserId, user.objectId)  //自己给教师的留言
            .include(ForThesis) //如果有关联关系，一定要添加include
            .findObjects(object : FindListener<Message>(){
                override fun done(p0: MutableList<Message>?, p1: BmobException?) {
                    if (p1 == null){
                        if (p0 != null && p0.isNotEmpty()){
                            _messagesLiveData.value = p0
                        }else{
                            callback.invoke("没有搜索到历史留言")
                        }
                    }else{
                        callback.invoke("出错了:${p1.message}")
                    }
                }
            })
    }


    /**  2.对留言进行 删除，拖拽排序  */
    private var messageAdapter:MessageAdapter? = null
    var messagesLiveData = _messagesLiveData

    fun initAdapter(
        userName:String,
        swipeRecyclerView: SwipeRecyclerView,
        onItemClickListener:OnItemClickCallback
    ){
        Log.v(LOG_TAG,"messagesLiveData:$messagesLiveData")
        messagesLiveData.value?.let {
            with(swipeRecyclerView){
                /**
                 * 第一次调用时没问题，但以后调用就会报下面的错，
                 * java.lang.IllegalStateException: Cannot set menu creator,
                 *      setAdapter has already been called.
                 * 每次调用都需要设置：swipeRecyclerView.adapter = null
                 */
                swipeRecyclerView.adapter = null
                setSwipeMenuCreator(swipeMenuCreator)
                setOnItemMenuClickListener(itemMenuClickListener)
                setOnItemMoveListener(itemMoveListener)

                isLongPressDragEnabled = true // 拖拽排序，默认关闭

                layoutManager = LinearLayoutManager(
                    getApplication(),
                    RecyclerView.VERTICAL,
                    false
                )
                messageAdapter = MessageAdapter(userName,it,onItemClickListener)
                adapter = messageAdapter
            }
        }
    }

    private val swipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->
        val swipeMenuItem = SwipeMenuItem(application).apply {
            setBackgroundColor(Color.parseColor("#FF0033"))
            text = "删除"
            setTextColor(Color.WHITE)
            textSize = 16
            height = 60*3
            width = 220
        }
        rightMenu.addMenuItem(swipeMenuItem)
    }

    private val itemMenuClickListener = OnItemMenuClickListener{menuBridge, adapterPosition ->
        menuBridge.closeMenu()
        if (menuBridge.direction == SwipeRecyclerView.RIGHT_DIRECTION){
            //菜单位置，如果有多个菜单项，需要判断
            val position = menuBridge.position
            messagesLiveData.value?.let {
                deleteMessage(it[adapterPosition]){
                    it.removeAt(adapterPosition)
                    messageAdapter?.notifyItemRemoved(adapterPosition)
                }
            }
            Log.v("cgf","adapterPosition=$adapterPosition 菜单位置:$position")
        }
    }

    private val itemMoveListener = object : OnItemMoveListener {
        override fun onItemMove(
            srcHolder: RecyclerView.ViewHolder?,
            targetHolder: RecyclerView.ViewHolder?
        ): Boolean {
            val srcPos = srcHolder?.adapterPosition
            val tarPos = targetHolder?.adapterPosition
            srcPos?.let {
                tarPos?.let {
                    messagesLiveData.value?.let {
                        Collections.swap(it,srcPos,tarPos)
                        messageAdapter?.notifyItemMoved(srcPos,tarPos)
                        //此时可以交换位置
                        return true
                    }
                }
            }
            return false  //不可以交换位置
        }

        /**
         * 侧滑直接删除，不显示提示删除的按钮
         * 需要配合 swipeRecyclerView.isItemViewSwipeEnabled = true，使用
         */
        override fun onItemDismiss(srcHolder: RecyclerView.ViewHolder?) {
//            srcHolder?.adapterPosition?.let {
//                Log.v("cgf","侧滑：${it}")
//                messageList?.removeAt(it)
//                adapter?.notifyItemRemoved(it)
//            }
        }
    }

    private fun deleteMessage(
        message: Message,
        callback:()->Unit,
    ) {
        val deleteMessage = Message()
        deleteMessage.objectId = message.objectId
        deleteMessage.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0 == null) callback.invoke()
                else Log.v(LOG_TAG,"删除留言失败：${p0.message}")
            }
        })
    }

    /**
     * @param userName  用户的名字
     * @param messageList 教师收到的留言或教师对课题的留言、除教师之外的角色
     * 给课题的留言
     */
    class MessageAdapter(
        private val userName:String,
        private val messageList: MutableList<Message>,
        private val onItemClickListener:OnItemClickCallback
    ):RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){
        class MessageViewHolder(val binding:MessageItemLayoutBinding): RecyclerView.ViewHolder(binding.root)
        {
            companion object{
                fun createMessageViewHolder(parent: ViewGroup):MessageViewHolder{
                    val inflater = LayoutInflater.from(parent.context)
                    val viewBinding = MessageItemLayoutBinding.inflate(inflater,parent,false)
                    return MessageViewHolder(viewBinding)
                }
            }

            fun bind(
                itemBinding: MessageItemLayoutBinding,
                userName: String,
                itemData: Message,
                onItemClickListener:OnItemClickCallback
            ){
                itemBinding.run {
                    message = itemData
                    if (userName == itemData.fUName){
                        topTitle.text = "你留言给:${itemData.forThesis.title}"
                    }else{
                        topTitle.text = "${itemData.fUName}留言给:${itemData.forThesis.title}"
                    }
                    root.setOnClickListener {
                        onItemClickListener.invoke(itemData)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return MessageViewHolder.createMessageViewHolder(parent)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.bind(holder.binding,userName,messageList[position],onItemClickListener)
        }

        override fun getItemCount() = messageList.size
    }
}
private typealias OnItemClickCallback = (message:Message)->Unit
private typealias FetchDataCallback = (msg: String) -> Unit

