package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bmob.databinding.FragmentTeacherSelectResultBinding


class TeacherSelectResultFragment : Fragment() {
    private lateinit var binding:FragmentTeacherSelectResultBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherSelectResultBinding.inflate(inflater,container,false)
        return binding.root
    }


}