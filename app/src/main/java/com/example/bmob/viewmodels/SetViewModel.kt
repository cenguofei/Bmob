package com.example.bmob.viewmodels

import android.Manifest
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.example.bmob.data.entity.STUDENT_NOT_SELECT_THESIS
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.fragments.mine.MineFragment.Companion.BMOB_USER_KEY
import com.example.bmob.fragments.mine.MineFragment.Companion.QUERY_USER_KEY
import com.example.bmob.fragments.mine.setting.SetFragment
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class SetViewModel(val handler: SavedStateHandle) : ViewModel() {
    private val repository = BmobRepository.getInstance()
    private var register: ActivityResultLauncher<Intent>? = null
    private var file: File? = null
    private var imageType: String? = null
    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore

    fun setSettingsDataStore(context: Context){
        this.settingsDataStore = SettingsDataStore.getInstance(context)
    }

    private fun saveUsernameToPreferencesStore(username:String,context: Context){
        viewModelScope.launch {
            settingsDataStore.saveUsernameToPreferencesStore(username = username, context = context)
        }
    }

    fun setRegister(fragment: SetFragment) {
        register = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            fragment.binding.progressBar.visibility = View.VISIBLE
            val data = it.data
            val resultCode = it.resultCode
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val uri = data?.data
                file = uriToFileQ(fragment.requireContext(), uri!!)
                Log.v(LOG_TAG, "uriToFileQ path = ${file?.path}  uri=$uri")
                //存储图片
                Log.v(LOG_TAG, "图片类型:${imageType}")

                //上传头像
                uploadImage(file!!, { isSuccess, msg ->
                    if (isSuccess) {
                        Log.v(LOG_TAG, "图片上传成功")
                        //改变ui
                        if ((imageType!!) == IMAGE_TYPE_HEAD) {
                            fragment.binding.editHeadIv.setImageURI(uri)
                        } else if ((imageType!!) == IMAGE_TYPE_BACKGROUND){
                            fragment.binding.backgroundIv.setImageURI(uri)
                        }
                    } else {
                        Log.v(LOG_TAG, "图片上传失败")
                        showMsg(fragment.requireContext(), msg)
                    }
                }) { progress ->
                    if (progress != null) {
                        //显示上传进度
                        Log.v(LOG_TAG, "progress=$progress")
                        fragment.binding.progressBar.progress = progress
                        if (progress == 100) {
                            fragment.binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    //打开相册选择图片
    fun openFile(imageType: String) {
        this.imageType = imageType
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        register?.launch(intent)
    }

    /**
     * 查询自定义用户User
     */
    fun getUserByQuery(): MutableLiveData<User> {
        if (!handler.contains(QUERY_USER_KEY)) {
            repository.getUserInfo { isSuccess, user ->
                if (isSuccess) {
                    handler.set(QUERY_USER_KEY, user)
                }
            }
        }
        return handler.getLiveData(QUERY_USER_KEY)
    }

    /**
     * 当用户切换身份登录时如果不刷新，会使用到前一个其他角色的用户，
     * 所以当要退出登录  或者切换账号时，
     * 需要清除当前用户信息
     */
    fun removeUser(){
        handler.remove<BmobUser>(BMOB_USER_KEY)
        handler.remove<User>(QUERY_USER_KEY)
    }

    /**
     * 查询BmobUser
     */
    fun getBmobUser(): MutableLiveData<BmobUser> {
        if (!handler.contains(BMOB_USER_KEY)) {
            val currentUser = BmobUser.getCurrentUser()
            handler.set(BMOB_USER_KEY, currentUser)
        }
        return handler.getLiveData(BMOB_USER_KEY)
    }

    //返回的progress可能为null  需要判断
    private fun uploadImage(
        file: File,
        callback: (isSuccess: Boolean, msg: String) -> Unit,
        progressCallback: (progress: Int?) -> Unit
    ) {
        Log.v(LOG_TAG, "开始上传图片")
        val bmobFile = BmobFile(file)
        bmobFile.uploadblock(object : UploadFileListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.v(LOG_TAG, "上传文件成功 url:${bmobFile.url}  fileUrl:${bmobFile.fileUrl}")
                    addImageUrlToCurrentUser(bmobFile.fileUrl) { isSuccess, message ->
                        if (isSuccess) {
                            Log.v(LOG_TAG, "type=$imageType 图片url已经添加到用户")
                            Log.v(LOG_TAG, "添加图片后的用户:${getUserByQuery().value.toString()}")
                            callback.invoke(true, EMPTY_TEXT)
                        } else {
                            Log.v(LOG_TAG, "type=$imageType 图片url没有添加到用户")
                            callback.invoke(false, message)
                        }
                    }
                } else {
                    Log.v(LOG_TAG, "上传文件失败 msg:${p0.message}")
                    callback.invoke(false, p0.message.toString())
                }
            }

            override fun onProgress(value: Int?) {
                Log.v(LOG_TAG, "pro value = $value")
                progressCallback.invoke(value)
            }
        })
    }

    fun addImageUrlToCurrentUser(
        fileUrl: String,
        callback: (isSuccess: Boolean, msg: String) -> Unit
    ) {
        getUserByQuery()
            .value?.run {
                Log.v(LOG_TAG, "找到的添加图片的用户:${this}")
                if (imageType == IMAGE_TYPE_HEAD) {
                    this.avatarUrl = fileUrl
                } else if (imageType == IMAGE_TYPE_BACKGROUND) {
                    this.backgroundUrl = fileUrl
                }
                update(object : UpdateListener() {
                    override fun done(p0: BmobException?) {
                        if (p0 == null) {
                            callback.invoke(true, EMPTY_TEXT)
                        } else {
                            callback.invoke(false, p0.message.toString())
                        }
                    }
                })
            }
//        repository.fetchUserInfo()
//        findCurrentUser { isSuccess, user, message ->
//            if (isSuccess) {
//                if (imageType == IMAGE_TYPE_HEAD){
//                    user!!.avatarUrl = fileUrl
//                    getUserByQuery().value?.avatarUrl = fileUrl
//                }else if (imageType == IMAGE_TYPE_BACKGROUND){
//                    user!!.backgroundUrl = fileUrl
//                    getUserByQuery().value?.backgroundUrl = fileUrl
//                }
//                user!!.update(object : UpdateListener() {
//                    override fun done(p0: BmobException?) {
//                        if (p0 == null) {
//                            callback.invoke(true, EMPTY_TEXT)
//                        } else {
//                            callback.invoke(false, p0.message.toString())
//                        }
//                    }
//                })
//            } else callback.invoke(false, message)
//        }
    }

    private fun findCurrentUser(callback: (isSuccess: Boolean, user: User?, msg: String) -> Unit) {
        if (!BmobUser.isLogin()) {
            callback.invoke(false, null, "当前用户未登录")
            return
        }
        BmobQuery<User>()
            .addWhereEqualTo("username", BmobUser.getCurrentUser().username)
            .findObjects(object : FindListener<User>() {
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if (p1 == null) {
                        if (p0 != null && p0.size == 1) {
                            callback.invoke(true, p0[0], EMPTY_TEXT)
                        } else if (p0 == null) callback.invoke(false, null, "没有搜索到当前用户")
                        else callback.invoke(false, null, "搜索到多个username相同的账户")
                    } else {
                        callback.invoke(false, null, p1.message.toString())
                    }
                }
            })
    }

    /**
     * 找到uri对应的文件
     */
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

    /**
     * 申请权限
     */
    fun requestPermissions(fragment: Fragment) {
        //申请权限
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Log.v(LOG_TAG, "用户拒绝权限请求")
            }
        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun saveUserEdit(fragment: SetFragment) {
        repository.getUserInfo { isSuccess, user ->
            if (isSuccess) {
                val userName = fragment.binding.editUsernameEv.text.toString()
                user?.run {
                    with(fragment.binding) {
                        name = editNameEv.text.toString()
                        signature = editSignatureEv.text.toString()
                        age = Integer.parseInt(editAgeEv.text.toString())
                        gender = editGenderEv.text.toString()
                        username = userName
                        school = editSchoolEv.text.toString()
                        college = editCollegeEv.text.toString()
                        department = editDepartmentEv.text.toString()
                        birth = editBirthEv.text.toString()
                        mobilePhoneNumber = editPhoneNumberEv.text.toString()
                        address = editAddressEv.text.toString()
                        email = editEmailEv.text.toString()
                    }
                }

                //SetFragment修改后让MineFragment接受到
                val bmobUser = BmobUser()
                bmobUser.username = userName
                bmobUser.mobilePhoneNumber = fragment.binding.editPhoneNumberEv.text.toString()
                bmobUser.email = fragment.binding.editEmailEv.text.toString()
                handler.set(BMOB_USER_KEY,bmobUser)
                handler.set(QUERY_USER_KEY,user)

                repository.updateUser(STUDENT_NOT_SELECT_THESIS,user!!) { isSuccess, msg ->
                    if (isSuccess) {
                        fragment.findNavController().navigateUp()
                        showMsg(fragment.requireContext(), "用户信息已更新")
                    } else {
                        showMsg(fragment.requireContext(), "用户信息更新失败:$msg")
                    }
                }

                //修改用户信息后，如果该用户是老师，就还要把老师对应课题的信息修改掉


                //修改了学生信息后，课题里面学生的信息也要修改


                saveUsernameToPreferencesStore(username = userName,fragment.requireContext())
            }
        }
    }
}

const val IMAGE_TYPE = "_image_type_"
const val IMAGE_TYPE_HEAD = "head_"
const val IMAGE_TYPE_BACKGROUND = "background_"
private const val EMPTY_TEXT = ""