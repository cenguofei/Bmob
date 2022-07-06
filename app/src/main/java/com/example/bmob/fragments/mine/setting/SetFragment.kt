package com.example.bmob.fragments.mine.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentSetBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.IMAGE_TYPE_BACKGROUND
import com.example.bmob.viewmodels.IMAGE_TYPE_HEAD
import com.example.bmob.viewmodels.SetViewModel

/**
 * 设置用户信息界面
 */
class SetFragment : Fragment() ,FragmentEventListener{
    lateinit var binding:FragmentSetBinding
    private val viewModel:SetViewModel by activityViewModels()
    private val args:SetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setRegister(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetBinding.inflate(inflater,container,false)
        binding.user = args.userInfo

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()

        Log.v(LOG_TAG,"SetFragment  viewModel:$viewModel")
    }

    override fun setEventListener() {
        //选择背景图片
        binding.backgroundIv.setOnClickListener {
            viewModel.openFile(IMAGE_TYPE_BACKGROUND)
        }
        //选择头像
        binding.editHeadIv.setOnClickListener {
            viewModel.openFile(IMAGE_TYPE_HEAD)
        }
    }
}