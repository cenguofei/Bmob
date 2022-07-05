package com.example.bmob.fragments.mine.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentSetBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SetViewModel
import java.io.File

/**
 * 设置用户信息界面
 */
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