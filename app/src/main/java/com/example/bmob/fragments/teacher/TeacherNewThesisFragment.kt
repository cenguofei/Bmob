package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentTeacherNewThesisBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.TeacherThesisViewModel

class TeacherNewThesisFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentTeacherNewThesisBinding
    private val viewModel:TeacherThesisViewModel by activityViewModels()
    private val setViewModel: SetViewModel by activityViewModels()
    private val args:TeacherNewThesisFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherNewThesisBinding.inflate(inflater,container,false)
        binding.teacherAvatarUrl = setViewModel.getUserByQuery().value!!.avatarUrl

        if (args.isUpdate){
            binding.updateButton.visibility = View.VISIBLE
            binding.ensureButton.visibility = View.GONE
        }

        viewModel.getThesis().observe(viewLifecycleOwner){
            Log.v(LOG_TAG,"教师选中thesis：$it")
            if (args.isUpdate){
                binding.thesis = it
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        //确定保存可以
        binding.ensureButton.setOnClickListener {
            try {
                val userLiveData = setViewModel.getUserByQuery()
                //仅当用户身份为教师时才能上传
                //前面我们已经确定过用户身份了，但是这里有必要再做一次检查
                userLiveData.value?.let {
                    if (viewModel.isInputValid(binding)){
                        viewModel.saveThesis(
                            userLiveData.value!!,
                            Thesis(
                                title = binding.thesisTitle.text.toString(),
                                field = binding.thesisField.text.toString(),
                                require = binding.thesisRequire.text.toString(),
                                description = binding.thesisBrief.text.toString()
                            )
                        ){isSuccess, msg ->
                            if (isSuccess){
                                showMsg(requireContext(),"课题上传成功")
                                back()
                            }else{
                                showMsg(requireContext(),"课题上传失败，请稍后再试:$msg")
                            }
                        }
                    }else{
                        showMsg(requireContext(),"请完善信息")
                    }
                }
//                if (userLiveData.value != null && userLiveData.value!!.identification == IDENTIFICATION_TEACHER){
//                    if (viewModel.isInputValid(binding)){
//                        viewModel.saveThesis(
//                            userLiveData.value!!,
//                            Thesis(
//                                title = binding.thesisTitle.text.toString(),
//                                field = binding.thesisField.text.toString(),
//                                require = binding.thesisRequire.text.toString(),
//                                description = binding.thesisBrief.text.toString()
//                            )
//                        ){isSuccess, msg ->
//                            if (isSuccess){
//                                showMsg(requireContext(),"课题上传成功")
//                                back()
//                            }else{
//                                showMsg(requireContext(),"课题上传失败，请稍后再试:$msg")
//                            }
//                        }
//                    }else{
//                        showMsg(requireContext(),"请完善信息")
//                    }
//                }
            }catch (e:Exception){
                Log.v(LOG_TAG,"TeacherNewThesis出错了：${e.message}")
            }
        }
        //取消课题
        binding.cancelBtn.setOnClickListener {
            back()
        }

        binding.updateButton.setOnClickListener {
            if (viewModel.isInputValid(binding)){
                viewModel.getThesis().value?.let {
                    it.title = binding.thesisTitle.text.toString()
                    it.field = binding.thesisField.text.toString()
                    it.require = binding.thesisRequire.text.toString()
                    it.description = binding.thesisBrief.text.toString()
                    viewModel.updateThesis(user = setViewModel.getUserByQuery().value!!, thesis = it){isSuccess, msg ->
                        if (isSuccess){
                            showMsg(requireContext(),"更新成功")
                        }else{
                            showMsg(requireContext(),"更新失败：$msg")
                        }
                    }
                }
            }else{
                showMsg(requireContext(),"请完善信息")
            }
        }
    }
    private fun back(){
        findNavController().navigateUp()
    }
}