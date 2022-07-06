package com.example.bmob.fragments.teacher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmob.R
import com.example.bmob.databinding.FragmentTeacherNewThesisBinding
import com.example.bmob.databinding.FragmentTeacherSelectResultBinding

class TeacherNewThesisFragment : Fragment() {
    private lateinit var binding:FragmentTeacherNewThesisBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherNewThesisBinding.inflate(inflater,container,false)
        return binding.root
    }
}