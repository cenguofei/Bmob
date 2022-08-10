package com.example.bmob.fragments.provost

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.FragmentSkimBinding
import com.example.bmob.databinding.ProvostSkimItemBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.viewmodels.ProvostSkimViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort


class SkimFragment : BaseVbFragment<FragmentSkimBinding>() {
    private val args: SkimFragmentArgs by navArgs()
    private val viewModel: ProvostSkimViewModel by activityViewModels()

    override fun createObserver() {
        showUserInfoByIdentification()
    }

    override fun setEventListener() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showUserInfoByIdentification() {
        when (args.identification) {
            IDENTIFICATION_STUDENT -> {
                binding.identificationTv.text = "学生信息"
                viewModel.getStudentInfoLiveData(appUser) { msg ->
                    showMsgShort(msg)
                }.observe(viewLifecycleOwner) { users -> initAdapter(users) }

            }
            IDENTIFICATION_TEACHER -> {
                binding.identificationTv.text = "教师信息"
                viewModel.getTeacherInfoLiveData(appUser) { msg ->
                    showMsgShort(msg)
                }.observe(viewLifecycleOwner) { users -> initAdapter(users) }
            }
            IDENTIFICATION_DEAN -> {
                binding.identificationTv.text = "系主任信息"
                viewModel.getDeanInfoLiveData(appUser) { msg ->
                    showMsgShort(msg)
                }.observe(viewLifecycleOwner) { users -> initAdapter(users) }
            }
        }
    }

    private fun initAdapter(users: MutableList<User>) {
        RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
            val itemInflater = LayoutInflater.from(parent.context)
            RecyclerViewAdapter.ResultViewHolder(
                ProvostSkimItemBinding.inflate(
                    itemInflater,
                    parent,
                    false
                )
            )
        }
        val adapter = RecyclerViewAdapter(users) { binding, result ->
            (binding as ProvostSkimItemBinding).user = result
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL, false
        )
        binding.recyclerView.adapter = adapter
    }
}