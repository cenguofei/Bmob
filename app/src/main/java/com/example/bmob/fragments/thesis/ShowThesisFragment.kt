package com.example.bmob.fragments.thesis

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentShowThesisBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.ObjectId
import com.example.bmob.utils.StudentThesis
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
            //判断时间
            viewModel.queryChooseThesisTime(setViewModel.getUserByQuery().value!!)
            {isSuccess, releaseTime,message ->
                if (isSuccess){
                    binding.timeIntervalTv.visibility = View.VISIBLE
                    binding.releaseTime = releaseTime
                }else{
//                    binding.participateButton.setBackgroundColor(R.color.grey_light)
                    Log.v(LOG_TAG,"isEnabled = false")
//                    binding.participateButton.isEnabled = false
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
            if (!TextUtils.isEmpty(binding.leaveMessageEt.text)){
                Log.v(LOG_TAG,"留言thesis：${args.thesis}")
                messageViewModel.uploadMessage(
                    args.thesis,
                    binding.leaveMessageEt.text.toString(),
                    setViewModel.getUserByQuery().value!!,
                    args.thesis.teacherId?:""
                ) { _, message ->
                    showMsg(requireContext(), message)
                }
            }else showMsg(requireContext(),"请输入留言内容")
        }
        binding.backBtn.setOnClickListener {
            Log.v(LOG_TAG,"返回按钮")
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
                            viewModel.fetchStudentThesis(stu){
                                setViewModel.setUserByQuery(it)
                            }
                        }
                    }
                }else showMsg(requireContext(),"当前不是选题时间")
            }else{
                showMsg(requireContext(),"不是选题时间段")
            }
        }
    }
}