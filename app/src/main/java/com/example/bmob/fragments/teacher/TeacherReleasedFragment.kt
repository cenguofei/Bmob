package com.example.bmob.fragments.teacher

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
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentTeacherReleasedBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.TeacherThesisViewModel


class TeacherReleasedFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentTeacherReleasedBinding
    private val thesisViewModel: TeacherThesisViewModel by activityViewModels()
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherReleasedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()

        thesisViewModel.getThesisList(setViewModel.getUserByQuery().value!!)
            .observe(viewLifecycleOwner) {
                val mAdapter = TeacherThesisAdapter(it) { thesis ->
                    val actionTeacherReleasedFragmentToTeacherNewThesisFragment =
                        TeacherReleasedFragmentDirections.actionTeacherReleasedFragmentToTeacherNewThesisFragment(
                            true
                        )
                    thesisViewModel.setThesis(thesis)
                    findNavController().navigate(
                        actionTeacherReleasedFragmentToTeacherNewThesisFragment
                    )
                }
                binding.recyclerView2.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                binding.recyclerView2.adapter = mAdapter
            }
    }

    override fun setEventListener() {
        binding.addNewThesis.setOnClickListener {
            findNavController().navigate(R.id.action_teacherReleasedFragment_to_teacherNewThesisFragment)
        }
    }
}