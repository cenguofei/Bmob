package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentTeacherReleasedBinding
import com.example.bmob.viewmodels.TeacherThesisViewModel


class TeacherReleasedFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentTeacherReleasedBinding
    private val viewModel:TeacherThesisViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherReleasedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun setEventListener() {
        binding.newThesisButton.setOnClickListener {
            findNavController().navigate(R.id.action_teacherReleaseFragment_to_teacherNewThesisFragment)
        }
    }
}