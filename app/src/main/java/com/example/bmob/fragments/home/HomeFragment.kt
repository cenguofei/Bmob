package com.example.bmob.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.data.entity.Message
import com.example.bmob.databinding.FragmentHomeBinding
import com.example.bmob.databinding.ItemDeanStudentSelectedBinding
import com.example.bmob.databinding.MessageItemLayoutBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel
import com.example.bmob.viewmodels.MessageViewModel
import com.example.bmob.viewmodels.SetViewModel
import java.lang.Exception


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private val userViewModel:BmobUserViewModel by activityViewModels()
    private val messageViewModel:MessageViewModel by viewModels()
    private val setViewModel:SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        userViewModel.getUserIdentification().observe(viewLifecycleOwner){identification->
            Log.v(LOG_TAG,"当前身份为：")
            try {
                when(identification){
                    IDENTIFICATION_TEACHER->{
                        messageViewModel.getTeacherRemoteHistoryMessage(
                            setViewModel.getUserByQuery().value!!
                        ){isSuccess, mutableListMessage, msg ->
                            Log.v(LOG_TAG,"历史留言：$mutableListMessage")
                            if (isSuccess){
                                setAdapter(mutableListMessage!!)
                            } else showMsg(requireContext(),msg)
                        }
                    }
                    else -> {
                        //显示该角色给课题的历史留言
                        messageViewModel.getRemoteHistoryMessage(setViewModel.getUserByQuery().value!!)
                        {isSuccess, mutableListMessage, msg ->
                            if (isSuccess){
                                setAdapter(mutableListMessage!!)
                            }else showMsg(requireContext(),msg)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return binding.root
    }

    private fun setAdapter(data:MutableList<Message>){
        RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent ->
            val itemInflater = LayoutInflater.from(parent.context)
            RecyclerViewAdapter.ResultViewHolder(
                MessageItemLayoutBinding.inflate(
                    itemInflater,
                    parent,
                    false
                )
            )
        }
        val adapter = RecyclerViewAdapter(data as List<Message>) { binding, result ->
            Log.v(LOG_TAG,"留言的title：${result.forThesis.title}\n\n" +
                    "留言的Thesis的objectId:${result.objectId}")
            (binding as MessageItemLayoutBinding).run {
                message = result
                if (setViewModel.getUserByQuery().value!!.name == result.fUName){
                    topTitle.text = "你留言给:${result.forThesis.title}"
                }else{
                    topTitle.text = "${result.fUName}留言给:${result.forThesis.title}"
                }
            }
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
}