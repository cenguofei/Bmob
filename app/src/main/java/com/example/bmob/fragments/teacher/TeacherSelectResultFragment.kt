package com.example.bmob.fragments.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentTeacherSelectResultBinding


class TeacherSelectResultFragment : Fragment(),FragmentEventListener{
    private lateinit var binding:FragmentTeacherSelectResultBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherSelectResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()


    }

    override fun setEventListener() {
        //老师导出学生选题信息
        binding.exportBtn.setOnClickListener {

        }
    }
}