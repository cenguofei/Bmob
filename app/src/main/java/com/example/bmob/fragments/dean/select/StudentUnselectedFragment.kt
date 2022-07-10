package com.example.bmob.fragments.dean.select

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
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.RecyclerViewAdapter
import com.example.bmob.databinding.FragmentStudentSelectedBinding
import com.example.bmob.databinding.FragmentStudentUnselectedBinding
import com.example.bmob.databinding.ItemDeanStudentNotSelectBinding
import com.example.bmob.databinding.ItemDeanStudentSelectedBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.DeanStudentSelectedViewModel
import com.example.bmob.viewmodels.SetViewModel


/**
 * 显示未选学生名单
 */
class StudentUnselectedFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentStudentUnselectedBinding
    private val viewModel: DeanStudentSelectedViewModel by viewModels()
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentUnselectedBinding.inflate(inflater, container, false)
        binding.dean = setViewModel.getUserByQuery().value
        viewModel.getStudentsWhichNotSelectedThesisLiveData(setViewModel.getUserByQuery().value!!,false){
            showMsg(requireContext(),it)
        }.observe(viewLifecycleOwner){
            Log.v(LOG_TAG,"StudentSelectedFragment观测到 未 选结果：$it")
            if (it.isNotEmpty()){
                RecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent->
                    val itemInflater = LayoutInflater.from(parent.context)
                    RecyclerViewAdapter.ResultViewHolder(ItemDeanStudentNotSelectBinding.inflate(itemInflater,parent,false))
                }
                val adapter = RecyclerViewAdapter(it){binding, result ->
                    (binding as ItemDeanStudentNotSelectBinding).user = result
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),
                    RecyclerView.VERTICAL,false)
                binding.recyclerView.adapter = adapter
            }else{
                showMsg(requireContext(),"没有搜索到任何结果")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {

    }
}