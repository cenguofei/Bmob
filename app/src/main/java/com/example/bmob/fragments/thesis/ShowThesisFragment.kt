package com.example.bmob.fragments.thesis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.FragmentShowThesisBinding
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
        binding.participateButton.setOnClickListener {
            setViewModel.getUserByQuery().value?.let {
                viewModel.addStudentToTeacherThesis(it,args.thesis){message: String ->

                }
            }
        }
    }
}