package com.example.bmob.fragments.message

import android.util.Log
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.FragmentMessageBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.myapp.appViewModel
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.MessageViewModel
import com.example.bmoblibrary.base.basefragment.BaseFragment
import com.example.bmoblibrary.ext.showMsgShort


class MessageFragment : BaseFragment<MessageViewModel, FragmentMessageBinding>() {

    override fun createObserver() {
        appViewModel.userIdentification.observe(viewLifecycleOwner) { identification ->
            Log.v(LOG_TAG, "当前身份为：$identification")
            when (identification) {
                IDENTIFICATION_TEACHER -> {
                    viewModel.getTeacherRemoteHistoryMessage(appUser) {
                        showMsgShort(it)
                    }
                }
                else -> {
                    //显示该角色给课题的历史留言
                    viewModel.getRemoteHistoryMessage(appUser) {
                        showMsgShort(it)
                    }
                }
            }
        }

        viewModel.messagesLiveData.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "接到通知，messageViewModel:$viewModel")
            viewModel.initAdapter(appUser.name ?: "", binding.swipeRecyclerView) {
                Log.v(LOG_TAG, "回调message")
            }
        }
    }
}