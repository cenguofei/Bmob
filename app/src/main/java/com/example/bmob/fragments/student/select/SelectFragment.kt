package com.example.bmob.fragments.student.select

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentSelectBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.StudentSelectViewModel

/**
 * 显示一个老师的所有课题
 */
class SelectFragment : Fragment(),FragmentEventListener {
    private val args:SelectFragmentArgs by navArgs()
    private val selectViewModel:StudentSelectViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectBinding.inflate(inflater,container,false)
        binding.thesisUser = args.teacher

        selectViewModel.getMutableTeacherThesisLiveData(args.teacher){
            showMsg(requireContext(),it)
        }.observe(viewLifecycleOwner){
            val adapter = StudentSelectThesisAdapter(it){thesis->
                val actionSelectFragmentToShowThesisFragment =
                    SelectFragmentDirections.actionSelectFragmentToShowThesisFragment(thesis, true)
                findNavController().navigate(actionSelectFragmentToShowThesisFragment)
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            binding.recyclerView.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.backImg.setOnClickListener { findNavController().navigateUp() }
    }
}