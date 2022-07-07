package com.example.bmob.fragments.teacher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentTeacherNewThesisBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.TeacherThesisViewModel

class TeacherNewThesisFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentTeacherNewThesisBinding
    private val viewModel:TeacherThesisViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherNewThesisBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun setEventListener() {
        //确定保存可以
        binding.ensureButton.setOnClickListener {
            if (viewModel.isInputValid(binding)){
                viewModel.saveThesis(
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
                        showMsg(requireContext(),"课题上传失败:$msg")
                    }
                }
            }
        }
        //取消课题
        binding.cancelButton.setOnClickListener {
            back()
        }
    }
    private fun back(){
        findNavController().navigateUp()
    }
}