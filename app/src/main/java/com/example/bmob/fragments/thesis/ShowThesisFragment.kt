package com.example.bmob.fragments.thesis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.bmob.v3.BmobObject
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentShowThesisBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.StudentSelectViewModel

class ShowThesisFragment : Fragment(),FragmentEventListener{
    private lateinit var binding:FragmentShowThesisBinding
    private val args:ShowThesisFragmentArgs by navArgs()
    private val viewModel:StudentSelectViewModel by viewModels()
    private val setViewModel:SetViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowThesisBinding.inflate(inflater,container,false)
        binding.thesis = args.thesis
        if (args.isShowParticipateButton){
            binding.participateButton.visibility = View.VISIBLE
        }else{
            binding.participateButton.visibility = View.GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.participateButton.setOnClickListener {
            setViewModel.getUserByQuery().value?.let {
                viewModel.addStudentToTeacherThesis(it,args.thesis,{isSuccess, message ->
                    showMsg(requireContext(), msg = message)
                }){student ->
                    setViewModel.setUserByQuery(student)
                    Log.v(LOG_TAG,"学生已更新：$student")
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
        }
    }
}
