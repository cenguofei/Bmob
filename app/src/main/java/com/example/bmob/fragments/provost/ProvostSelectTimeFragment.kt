package com.example.bmob.fragments.provost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentProvostSelectTimeBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.ProvostViewModel
import com.example.bmob.viewmodels.SetViewModel


class ProvostSelectTimeFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentProvostSelectTimeBinding
    private val setViewModel: SetViewModel by activityViewModels()
    private val viewModel: ProvostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProvostSelectTimeBinding.inflate(inflater, container, false)
        binding.provost = setViewModel.getUserByQuery().value
        viewModel.getProvostReleaseSelectTimeLiveData(setViewModel.getUserByQuery().value!!)
            .observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.releaseTimeEntity = it
                    binding.confirmBtn.visibility = View.GONE
                    binding.updateBtn.visibility = View.VISIBLE
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding.timeStartLayout.setOnClickListener {
            setViewModel.selectTime(requireContext(), "开始时间", 3, 3, 3) {
                binding.chooseStartTime.text = it
            }
        }
        binding.timeEndLayout.setOnClickListener {
            setViewModel.selectTime(requireContext(), "结束时间", 0, 0, 0) {
                binding.chooseEndTime.text = it
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.confirmBtn.setOnClickListener {
            binding.confirmBtn.isEnabled = false
            Log.v(LOG_TAG, "被点击了")
            val beginTime = binding.chooseStartTime.text.toString()
            val endTime = binding.chooseEndTime.text.toString()
            viewModel.checkIsEndValid(
                beginTime,
                endTime
            ) { isValid, message ->
                Log.v(LOG_TAG, "回调")
                if (!isValid) {
                    binding.confirmBtn.isEnabled = true
                    showMsg(requireContext(), message)
                } else {
                    //保存发布时间
                    Log.v(LOG_TAG, "日期合法")
                    viewModel.saveTime(beginTime, endTime, setViewModel.getUserByQuery().value!!)
                    { isSuccess, msg ->
                        showMsg(requireContext(), msg)
                        if (isSuccess) {
                            binding.confirmBtn.visibility = View.GONE
                            binding.updateBtn.visibility = View.VISIBLE
                        } else {
                            binding.confirmBtn.isEnabled = true
                        }
                    }
                }
            }
        }
        binding.updateBtn.setOnClickListener {
            //更新时间
            val beginTime = binding.chooseStartTime.text.toString()
            val endTime = binding.chooseEndTime.text.toString()
            viewModel.checkIsEndValid(
                beginTime,
                endTime
            ) { isValid, message ->
                Log.v(LOG_TAG, "回调")
                if (!isValid) {
                    showMsg(requireContext(), message)
                } else {
                    setViewModel.getUserByQuery().value?.let {
                        //保存发布时间
                        viewModel.updateReleaseTime(
                            viewModel.getProvostReleaseSelectTimeLiveData(it).value!!,
                            beginTime,
                            endTime
                        ) { msg ->
                            showMsg(requireContext(), msg)
                        }
                    }
                }
            }
        }
    }
}