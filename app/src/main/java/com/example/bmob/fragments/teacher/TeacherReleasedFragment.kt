package com.example.bmob.fragments.teacher

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentTeacherReleasedBinding
import com.example.bmob.databinding.ItemTeacherReleaseBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.viewmodels.TeacherThesisViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment


class TeacherReleasedFragment : BaseVbFragment<FragmentTeacherReleasedBinding>() {
    private val thesisViewModel: TeacherThesisViewModel by activityViewModels()

    override fun createObserver() {
        thesisViewModel.getThesisList(appUser).observe(viewLifecycleOwner) { initAdapter(it) }
    }

    override fun setEventListener() {
        binding.addNewThesis.setOnClickListener {
            findNavController().navigate(R.id.action_teacherReleasedFragment_to_teacherNewThesisFragment)
        }
        binding.imageView12.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initAdapter(thesisList: MutableList<Thesis>) {
        RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
            val itemInflater = LayoutInflater.from(parent.context)
            RecyclerViewAdapter.ResultViewHolder(
                ItemTeacherReleaseBinding.inflate(
                    itemInflater,
                    parent,
                    false
                )
            )
        }
        val adapter = RecyclerViewAdapter(thesisList) { binding, result ->
            (binding as ItemTeacherReleaseBinding).run {
                thesis = result
                val actionTeacherReleasedFragmentToTeacherNewThesisFragment =
                    TeacherReleasedFragmentDirections
                        .actionTeacherReleasedFragmentToTeacherNewThesisFragment(true)
                root.setOnClickListener {
                    thesisViewModel.setThesis(result)
                    findNavController().navigate(
                        actionTeacherReleasedFragmentToTeacherNewThesisFragment
                    )
                }
            }
        }
        binding.swipeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.swipeRecyclerView.adapter = adapter
    }
}