package com.example.bmob.fragments.dean.select

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentStudentSelectedBinding
import com.example.bmob.databinding.FragmentStudentUnselectedBinding
import com.example.bmob.viewmodels.DeanStudentSelectedViewModel


/**
 * 显示已选学生名单
 */
class StudentSelectedFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentStudentSelectedBinding
    private val viewModel:DeanStudentSelectedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentSelectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()

    }

    override fun setEventListener() {

    }
}