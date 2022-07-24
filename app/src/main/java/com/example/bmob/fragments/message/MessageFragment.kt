package com.example.bmob.fragments.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.FragmentMessageBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmob.viewmodels.MessageViewModel
import com.example.bmob.viewmodels.SetViewModel


class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private val userViewModel: BmobUserViewModel by activityViewModels()
    private val messageViewModel: MessageViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        userViewModel.getUserIdentification().observe(viewLifecycleOwner) { identification ->
            Log.v(LOG_TAG, "当前身份为：$identification")
            when (identification) {
                IDENTIFICATION_TEACHER -> {
                    setViewModel.getUserByQuery().value?.let { teacher ->
                        messageViewModel.getTeacherRemoteHistoryMessage(teacher) {
                            showMsg(requireContext(), it)
                        }
                    }
                }
                else -> {
                    //显示该角色给课题的历史留言
                    setViewModel.getUserByQuery().value?.let { user ->
                        messageViewModel.getRemoteHistoryMessage(user) {
                            showMsg(requireContext(), it)
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageViewModel.messagesLiveData.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "接到通知，messageViewModel:$messageViewModel")
            setViewModel.getUserByQuery().value?.let { user ->
                messageViewModel.initAdapter(user.name ?: "", binding.swipeRecyclerView) {
                    Log.v(LOG_TAG, "回调message")
                }
            }
        }
    }
}