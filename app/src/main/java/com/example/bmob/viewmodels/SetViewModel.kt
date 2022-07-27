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
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.example.bmob.R
import com.example.bmob.data.entity.ReleaseTime
import com.example.bmob.data.entity.User
import com.example.bmob.data.repository.remote.BmobRepository
import com.example.bmob.data.storage.SettingsDataStore
import com.example.bmob.fragments.mine.MineFragment.Companion.BMOB_USER_KEY
import com.example.bmob.fragments.mine.MineFragment.Companion.QUERY_USER_KEY
import com.example.bmob.fragments.mine.setting.SetFragment
import com.example.bmob.utils.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class SetViewModel(val handler: SavedStateHandle) : ViewModel() {
    private val repository = BmobRepository.getInstance()
    private var register: ActivityResultLauncher<Intent>? = null
    private var file: File? = null
    private var imageType: String? = null

    //用户配置，记住密码，保存账号密码等
    private lateinit var settingsDataStore: SettingsDataStore

    /**
     * 初始化dataStore
     */
    fun setSettingsDataStore(context: Context) {
        this.settingsDataStore = SettingsDataStore.getInstance(context)
    }

    /**
     * 保存用户名到dataStore
     */
    private fun saveUsernameToPreferencesStore(username: String, context: Context) {
        viewModelScope.launch {
            settingsDataStore.saveUsernameToPreferencesStore(username = username, context = context)
        }
    }

    /**
     * 初始化register，
     * 必须要在onCreate的时候调用
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setRegister(fragment: SetFragment) {
        register = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val data = it.data
            val resultCode = it.resultCode
            if (resultCode == AppCompatActivity.RESULT_OK) {
                data?.data?.let { uri ->
                    fragment.binding.progressBar.visibility = View.VISIBLE
                    file = uriToFileQ(fragment.requireContext(), uri)
                    //上传头像
                    uploadImage(file!!, { isSuccess, msg ->
                        if (isSuccess) {
                            //改变ui
                            if ((imageType!!) == IMAGE_TYPE_HEAD) {
                                fragment.binding.editHeadIv.setImageURI(uri)
                            } else if ((imageType!!) == IMAGE_TYPE_BACKGROUND) {
                                fragment.binding.backgroundIv.setImageURI(uri)
                            }
                        } else {
                            showMsg(fragment.requireContext(), msg)
                        }
                    }) { progress ->
                        if (progress != null) {
                            //显示上传进度
                            fragment.binding.progressBar.progress = progress
                            if (progress == 100) {
                                fragment.binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 打开相册选择图片
     */
    fun openFile(imageType: String) {
        this.imageType = imageType
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        register?.launch(intent)
    }

    /**
     * 查询自定义用户User
     *
     * 关键方法，全局都会用到
     */
    fun getUserByQuery(): MutableLiveData<User> {
        Log.v(LOG_TAG, "getUserByQuery(): MutableLiveData<User>")
        if (!handler.contains(QUERY_USER_KEY)) {
            repository.getUserInfo { isSuccess, user ->
                if (isSuccess && user != null) {
                    handler.set(QUERY_USER_KEY, user)
                }
            }
        }
        return handler.getLiveData(QUERY_USER_KEY)
    }

    fun setUserByQuery(user: User) {
        handler.set(QUERY_USER_KEY, user)
    }

    /**
     * 当用户切换身份登录时如果不刷新，会使用到前一个其他角色的用户，
     * 所以当要退出登录  或者切换账号时，
     * 需要清除当前用户信息
     */
    fun removeUser() {
        handler.remove<BmobUser>(BMOB_USER_KEY)
        handler.remove<User>(QUERY_USER_KEY)
        BmobUser.logOut()
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

    /**
     * 返回的progress可能为null  需要判断
     */
    private fun uploadImage(
        file: File,
        callback: (isSuccess: Boolean, msg: String) -> Unit,
        progressCallback: (progress: Int?) -> Unit
    ) {
        val bmobFile = BmobFile(file)
        bmobFile.uploadblock(object : UploadFileListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    addImageUrlToCurrentUser(bmobFile.fileUrl) { isSuccess, message ->
                        if (isSuccess) {
                            Log.v(LOG_TAG, "type=$imageType 图片url已经添加到用户")
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
                progressCallback.invoke(value)
            }
        })
    }

    /**
     * 上传头像或背景后保存url到用户
     */
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

    fun saveUserEdit(
        fragment: SetFragment,
        callback: () -> Unit
    ) {
        getUserByQuery().value?.let {
            val userName = fragment.binding.editUsernameEv.text.toString()
            it.run {
                with(fragment.binding) {
                    name = editNameEv.text.toString()
                    signature = editSignatureEv.text.toString()
                    nickname = editNicknameEv.text.toString()
                    gender = editGenderEv.text.toString()
                    username = userName
                    school = editSchoolEv.text.toString()
                    college = editCollegeEv.text.toString()
                    department = editDepartmentEv.text.toString()

                    val birthFormat = editBirthEv.text.toString().split(" ")[0]
                    birth = birthFormat

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

            handler.set(BMOB_USER_KEY, bmobUser)
            handler.set(QUERY_USER_KEY, it)

            saveUsernameToPreferencesStore(username = userName, fragment.requireContext())

            repository.updateUser(it) { isResponseSuccess, msg ->
                if (isResponseSuccess) {
                    showMsg(fragment.requireContext(), "用户信息已更新")
                    callback.invoke()
                } else {
                    showMsg(fragment.requireContext(), "用户信息更新失败:$msg")
                }
            }

            //修改用户信息后，如果该用户是老师，就还要把老师对应课题的信息修改掉


            //修改了学生信息后，课题里面学生的信息也要修改
        }
    }

    /**
     * 针对学生，当没有开放选题时间时，
     * 学生已选的课题状态为不可用，否则可用
     */
    /**
     * 判断当前时间是否为课题可选时间
     */
    fun isSelectTime(
        student: User,
        callback: (isSelectTime: Boolean, message: String) -> Unit,
    ) {
        BmobQuery<ReleaseTime>()
            .addWhereEqualTo(School, student.school)
            .setLimit(1)
            .findObjects(object : FindListener<ReleaseTime>() {
                override fun done(p0: MutableList<ReleaseTime>?, p1: BmobException?) {
                    if (p1 == null && p0 != null && p0.isNotEmpty()) {
                        if (measureIsNowInSelectTime(p0[0]) { callback.invoke(false, it) }) {
                            callback.invoke(true, EMPTY_TEXT)
                        }else callback.invoke(false, EMPTY_TEXT)
                    } else {
                        callback.invoke(false, p1?.message.toString())
                    }
                }
            })
    }

    private fun measureIsNowInSelectTime(
        releaseTime: ReleaseTime,
        callback: (message: String) -> Unit
    ): Boolean {
        return try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val beginTime = simpleDateFormat.parse(releaseTime.beginTime)
            val endTime = simpleDateFormat.parse(releaseTime.endTime)

            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH) + 1
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val minute: Int = calendar.get(Calendar.MINUTE)
            val second: Int = calendar.get(Calendar.SECOND)

            val dateSystem = simpleDateFormat.parse("$year-$month-$day $hour:$minute:$second")

            //还没到选题时间
            if (dateSystem != null) {
                if (dateSystem.before(beginTime)) {
                    return false
                }
            }
            //选题时间已经过了
            if (dateSystem != null) {
                if (dateSystem.after(endTime)) {
                    return false
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }

    /**
     * 设置时间选择器
     */
    fun selectTime(
        context: Context,
        title: String,
        monthOff: Int,
        dayOff: Int,
        hourOff: Int,
        callback: (time: String) -> Unit
    ) {
        val calendar: Calendar = Calendar.getInstance()
        var yearBegin: Int = calendar.get(Calendar.YEAR)
        var monthBegin: Int = calendar.get(Calendar.MONTH) + 1
        var dayBegin: Int = calendar.get(Calendar.DAY_OF_MONTH)
        var hourBegin: Int = calendar.get(Calendar.HOUR_OF_DAY)
        var minuteBegin: Int = calendar.get(Calendar.MINUTE)

        val view =
            View.inflate(context.applicationContext, R.layout.item_select_time, null)
        val datePicker = view.findViewById<View>(R.id.new_act_date_picker) as DatePicker
        val timePicker = view.findViewById<View>(R.id.new_act_time_picker) as TimePicker

        datePicker.init(yearBegin, monthBegin - 1, dayBegin, null)
        timePicker.setIs24HourView(true)
        timePicker.hour = hourBegin
        timePicker.minute = minuteBegin

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setView(view)
            .setTitle(title)
            .setPositiveButton("确定") { dialog, which ->
                Log.v(LOG_TAG, "dialog=$dialog,which=$which")
                yearBegin = datePicker.year
                monthBegin = datePicker.month + 1
                dayBegin = datePicker.dayOfMonth
                hourBegin = timePicker.hour-1
                minuteBegin = timePicker.minute-1
                val dateString = "$yearBegin-$monthBegin-$dayBegin $hourBegin:$minuteBegin:00"
                Log.v(LOG_TAG, "选择的时间1：$dateString")
                callback.invoke(dateString)
            }
        builder.show()
        val dateString = "$yearBegin-$monthBegin-$dayBegin $hourBegin:$minuteBegin:00"
        Log.v(LOG_TAG, "选择的时间2：$dateString")
    }

    companion object{
        private const val IMAGE_TYPE_HEAD = "head_"
        private const val IMAGE_TYPE_BACKGROUND = "background_"
    }
}
