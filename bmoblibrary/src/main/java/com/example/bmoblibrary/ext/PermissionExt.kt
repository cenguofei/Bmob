package com.example.bmoblibrary.ext

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

@RequiresApi(Build.VERSION_CODES.R)
fun Fragment.askPermission() {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        //通过的权限
        val grantedList = it.filterValues { booleanIt ->
            booleanIt
        }.mapNotNull { entryIt ->
            entryIt.key
        }
        //是否所有权限都通过
        val allGranted = grantedList.size == it.size
        val list = (it - grantedList.toSet()).map { entryIt ->
            entryIt.key
        }
        //未通过的权限
        val deniedList = list.filter { stringIt ->
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), stringIt)
        }
        //拒绝并且点了“不再询问”权限
        val alwaysDeniedList = list - deniedList.toSet()
    }.launch(
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        )
    )
}