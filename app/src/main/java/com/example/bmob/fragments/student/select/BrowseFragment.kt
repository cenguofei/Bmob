package com.example.bmob.fragments.student.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentBrowseBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.StudentSelectViewModel

/**
 * 选择课题的页面
 */
class BrowseFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentBrowseBinding
    private val selectViewModel:StudentSelectViewModel by viewModels()

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
        selectViewModel.findAllTeacherInDepartment{isSuccess, thesisList, msg ->
            if (isSuccess){
                val studentSelectRecyclerViewAdapter =
                    StudentSelectRecyclerViewAdapter(thesisList!!) {
//                        val actionBrowseFragmentToSelectFragment =
//                            SelectFragmentDirections.actionBrowseFragmentToSelectFragment(it)
//                        findNavController().navigate(actionBrowseFragmentToSelectFragment)
                    }
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),
                    RecyclerView.VERTICAL,false
                )
                binding.recyclerView.adapter = studentSelectRecyclerViewAdapter
            }else{
                showMsg(requireContext(),msg)
            }
        }
    }

    override fun setEventListener() {
        binding.backImg.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}