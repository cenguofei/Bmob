package com.example.bmob.fragments.student.select

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentBrowseBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.StudentSelectViewModel

/**
 * 浏览有课题的各个老师
 */
class BrowseFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentBrowseBinding
    private val selectViewModel:StudentSelectViewModel by activityViewModels()
    private val setViewModel:SetViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrowseBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()

        //设置选题学生的校院系
        setViewModel.getUserByQuery().observe(viewLifecycleOwner){
            binding.user = it
        }

        selectViewModel.getAllTeacherInDepartmentLiveData(setViewModel.getUserByQuery().value!!){
            showMsg(requireContext(),it)
        }.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                Log.v(LOG_TAG,"BrowseFragment查询可选课题的老师的课题成功了")
                val browseTeacherHasThesisAdapter =
                    BrowseTeacherHasThesisAdapter(it!!) {teacher->
                        Log.v(LOG_TAG,"被点击：$teacher")
                        val actionBrowseFragmentToSelectFragment =
                            BrowseFragmentDirections.actionBrowseFragmentToSelectFragment(teacher)
                        findNavController().navigate(actionBrowseFragmentToSelectFragment)
                    }
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),
                    RecyclerView.VERTICAL,false
                )
                binding.recyclerView.adapter = browseTeacherHasThesisAdapter
            }
        }
    }

    override fun setEventListener() {
        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}