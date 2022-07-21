package com.example.exceltestmodule

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.exceltestmodule.databinding.ActivityFullscreenBinding
import com.example.exceltestmodule.utils.ExcelUtil
import com.example.exceltestmodule.utils.ProjectBean
import java.io.File
import java.io.FileOutputStream

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        application
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.fullscreenContent
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(delayHideTouchListener)

        binding.testBtn.setOnClickListener {
            val sdCardDir = Environment.getStorageDirectory()
            val saveFile = File(sdCardDir, "gg11.txt")
            Log.v("cgf",saveFile.path)
            if (!saveFile.exists()){
                saveFile.createNewFile()
            }
            val outStream = FileOutputStream(saveFile)
            outStream.write(1)
            outStream.close()
        }

        askPermission()

        binding.exportBtn.setOnClickListener {

            var filePath = "${getExternalFilesDir("bmobExcel")?.path}"
            Log.v("cgf","filePath=$filePath")

            if (checkPermission()){
                filePath = Environment.getExternalStorageDirectory().toString()
                Log.v("cgf","外部存储：$filePath")
            }else{
                Log.v("cgf","应用内存储：$filePath")
            }

            val file = File(filePath)
            if (!file.exists()) {
                Log.v("cgf","没有文件夹，开始创建")
                file.mkdirs()
            }else Log.v("cgf","有文件夹，不创建")
            val excelFileName = "demo.xlsx"
            val  colsTitleName = arrayOf("name","project","money","year","month","day","beiZhu")

            val demoBeanList = arrayListOf<ProjectBean>().apply {
                add(ProjectBean("名字1","pro","money","2020","12月","1day","备注"))
                add(ProjectBean("名字2","pro","money","2020","12月","1day","备注"))
                add(ProjectBean("名字3","pro","money","2020","12月","1day","备注"))
                add(ProjectBean("名字4","pro","money","2020","12月","1day","备注"))
                add(ProjectBean("名字5","pro","money","2020","12月","1day","备注"))
            }

            filePath = "$filePath/$excelFileName"


            ExcelUtil.initExcel(filePath, colsTitleName,excelFileName.substring(0,excelFileName.length-5))

            ExcelUtil.writeObjListToExcel(demoBeanList, filePath, this)
            Log.v("cgf","filePath=$filePath")
        }
    }

    private fun checkPermission():Boolean{
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun askPermission(){
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            //通过的权限
            val grantedList = it.filterValues { booleanIt->
                booleanIt
            }.mapNotNull { entryIt->
                entryIt.key
            }
            //是否所有权限都通过
            val allGranted = grantedList.size == it.size
            val list = (it - grantedList.toSet()).map {entryIt->
                entryIt.key
            }
            //未通过的权限
            val deniedList = list.filter { stringIt->
                ActivityCompat.shouldShowRequestPermissionRationale(this, stringIt)
            }
            //拒绝并且点了“不再询问”权限
            val alwaysDeniedList = list - deniedList.toSet()
        }.launch(arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        ))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}