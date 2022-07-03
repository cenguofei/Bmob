package com.example.bmob.fragments.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmob.R
import com.example.bmob.databinding.FragmentStudentWorkBinding

class StudentWorkFragment : Fragment() {
    private lateinit var binding:FragmentStudentWorkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentWorkBinding.inflate(inflater,container,false)
        return binding.root
    }
}