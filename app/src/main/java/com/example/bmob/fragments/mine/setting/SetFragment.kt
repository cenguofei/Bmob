package com.example.bmob.fragments.mine.setting

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentSetBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SetViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class SetFragment : Fragment() ,FragmentEventListener{
    private lateinit var binding:FragmentSetBinding
    private var register: ActivityResultLauncher<Intent>? = null
    private var file:File? = null
    private val viewModel:SetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        register = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val data = it.data
            val resultCode = it.resultCode
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val uri = data?.data
                file = viewModel.uriToFileQ(requireContext(), uri!!)
                Log.v(LOG_TAG,"uriToFileQ path = ${file?.path}  uri=${uri.toString()}")
            }
        }

        //申请权限
//        registerForActivityResult(ActivityResultContracts.RequestPermission()){
//            if (!it){
//                Log.v(LOG_TAG,"用户拒绝权限请求")
//            }
//        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {

    }
}