package com.example.bmob.fragments.thesis

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import cn.bmob.v3.BmobObject
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentShowThesisBinding
import com.example.bmob.databinding.LeaveMessagePopupWindowLayoutBinding
import com.example.bmob.databinding.SexPopupWindowBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.MessageViewModel
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.StudentSelectViewModel

class ShowThesisFragment : Fragment(),FragmentEventListener{
    private lateinit var binding:FragmentShowThesisBinding
    private val args:ShowThesisFragmentArgs by navArgs()
    private val viewModel:StudentSelectViewModel by activityViewModels()
    private val setViewModel:SetViewModel by activityViewModels()

    private val messageViewModel:MessageViewModel by viewModels()
//    private lateinit var popupWindowBinding:LeaveMessagePopupWindowLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowThesisBinding.inflate(inflater,container,false)
        binding.thesis = args.thesis
        Log.v(LOG_TAG,"args.thesis:${args.thesis}")
        /**
         * 判断当前用户身份
         * 是学生则显示加入按钮，否则隐藏
         */
        if (args.isShowParticipateButton){
            binding.participateButton.visibility = View.VISIBLE
            viewModel.queryChooseThesisTime(setViewModel.getUserByQuery().value!!)
            {isSuccess, releaseTime,message ->
                if (isSuccess){
                    binding.timeIntervalTv.visibility = View.VISIBLE
                    binding.releaseTime = releaseTime
                }else{
                    binding.participateButton.setBackgroundColor(R.color.grey_light)
                    Log.v(LOG_TAG,"isEnabled = false")
                    showMsg(requireContext(),message)
                }
            }
        }else{
            binding.participateButton.visibility = View.GONE
        }

        /**
         * 判断是不是学生用户进入选题
         * 如果不是选题时间，就让加入按钮失效
         */
//        viewModel.isStudentSelectThesis.observe(viewLifecycleOwner){
//            Log.v(LOG_TAG,"isStudentSelectThesis:$it")
//            if (it){
//                setViewModel.isSelectTime(setViewModel.getUserByQuery().value!!){isSelectTime, message ->
//                    if (!isSelectTime){
//                        showMsg(requireContext(),message)
//                        binding.participateButton.setBackgroundColor(R.color.grey_light)
//                        binding.participateButton.isEnabled = false
//                    }
//                }
//            }
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.leaveMessageBtn.setOnClickListener {
            Log.v(LOG_TAG,"留言thesis：${args.thesis}")
            messageViewModel.uploadMessage(
                args.thesis,
                binding.leaveMessageEt.text.toString(),
                setViewModel.getUserByQuery().value!!,
                args.thesis.teacherId?:""
            ) { _, message ->
                showMsg(requireContext(), message)
            }
        }
//        val from = LayoutInflater.from(requireContext())
//        popupWindowBinding = LeaveMessagePopupWindowLayoutBinding.inflate(from, null, false)
//        val popupWindow = PopupWindow(
//            popupWindowBinding.root,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true
//        )
//        //取消留言
//        popupWindowBinding.cancel.setOnClickListener {
//            popupWindow.dismiss()
//        }
//        //上传留言
//        popupWindowBinding.confirm.setOnClickListener {
//            popupWindow.dismiss()
//            messageViewModel.uploadMessage(
//                args.thesis,
//                binding.leaveMessageEt.text.toString(),
//                setViewModel.getUserByQuery().value!!,
//                args.thesis.teacherId!!
//            ){_, message ->
//                showMsg(requireContext(),message)
//            }
//        }

        //留言
//        binding.leaveMessageEt.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEND){
//                popupWindow.showAsDropDown(v)
//            }
//            true
//        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.participateButton.setOnClickListener {
            //如果有时间区间，先检查当前时间是否在可选范围内
            if (viewModel.releaseTime != null){
                if (viewModel.isInSelectTime(viewModel.releaseTime!!)){
                    setViewModel.getUserByQuery().value?.let { student->
                        viewModel.addStudentToTeacherThesis(student,args.thesis,{msg->
                            showMsg(requireContext(),msg)
                        }){stu ->
                            setViewModel.setUserByQuery(stu)
                            Log.v(LOG_TAG,"学生已更新：$stu")
                        }
                    }
                }else showMsg(requireContext(),"当前不是选题时间")
            }else{
                showMsg(requireContext(),"不是选题时间段")
            }
        }
    }
}

//            //测试
//            Test("测试Thesis",args.thesis)
//                .save(object :SaveListener<String>(){
//                    override fun done(p0: String?, p1: BmobException?) {
//                        if (p1 == null){
//                            Log.v(LOG_TAG,"保存测试成功")
//                        }else{
//                            Log.v(LOG_TAG,"保存测试失败：${p1.message}")
//                        }
//                    }
//                })

//            Thesis("测试","测试","测试","测试","测试","测试",
//                mutableListOf(),false,2,2,"测试","测试","测试",
//                "测试","测试",true,2
//            ).save(object :SaveListener<String>(){
//                override fun done(p0: String?, p1: BmobException?) {
//                    if (p1 == null) {
//                        Log.v(LOG_TAG, "保存测试成功")
//                    } else {
//                        Log.v(LOG_TAG, "保存测试失败：${p1.message}")
//                    }
//                }
//            })