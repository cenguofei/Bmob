package com.example.bmob.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.example.bmob.data.entity.User
import com.example.bmob.utils.LOG_TAG
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class SetViewModel : ViewModel() {

    //返回的progress可能为null  需要判断
    fun uploadImage(view: View, file: File,callback:(isSuccess:Boolean,progress:Int?,msg:String)->Unit) {
        val bmobFile = BmobFile(file)
        bmobFile.uploadblock(object : UploadFileListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.v(LOG_TAG, "上传文件成功 url:${bmobFile.url}  fileUrl:${bmobFile.fileUrl}")
                    addImageToCurrentUser(bmobFile.fileUrl){isSuccess, message ->
                        if (isSuccess){
                            callback.invoke(true,null, EMPTY_TEXT)
                        }else{
                            callback.invoke(false,null,message)
                        }
                    }
                } else {
                    Log.v(LOG_TAG, "上传文件失败 msg:${p0.message}")
                    callback.invoke(false,null,p0.message.toString())
                }
            }
            override fun onProgress(value: Int?) {
                callback.invoke(false,value, EMPTY_TEXT)
            }
        })
    }

    fun addImageToCurrentUser(fileUrl: String,callback: (isSuccess: Boolean,msg: String) -> Unit){
        findCurrentUser{isSuccess, user, message ->
            if (isSuccess){
                user!!.avatarUrl = fileUrl
                user.update(object :UpdateListener(){
                    override fun done(p0: BmobException?) {
                        if (p0 == null){
                            callback.invoke(true, EMPTY_TEXT)
                        }else{
                            callback.invoke(false,p0.message.toString())
                        }
                    }
                })
            }else callback.invoke(false,message)
        }
    }

    private fun findCurrentUser(callback:(isSuccess:Boolean, user:User?, msg:String)->Unit) {
        if (!BmobUser.isLogin()){
            callback.invoke(false,null,"当前用户未登录")
            return
        }
        BmobQuery<User>()
            .addWhereEqualTo("username",BmobUser.getCurrentUser().username)
            .findObjects(object : FindListener<User>() {
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if (p1 == null){
                    if (p0 != null && p0.size == 1){
                        callback.invoke(true,p0[0], EMPTY_TEXT)
                    }else if (p0 == null) callback.invoke(false,null,"没有搜索到当前用户")
                    else callback.invoke(false,null,"搜索到多个username相同的账户")
                }else{
                    callback.invoke(false,null,p1.message.toString())
                }
            }
        })
    }

    //找到uri对应的文件
    @RequiresApi(Build.VERSION_CODES.Q)
    fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            File(requireNotNull(uri.path))
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = "${System.currentTimeMillis()}${Random.nextInt(0, 9999)}.${
                MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(contentResolver.getType(uri))
            }"

            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null

    //打开相册选择图片
    fun openFile(register: ActivityResultLauncher<Intent>?) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        register?.launch(intent)
    }
}

private const val EMPTY_TEXT = ""