package com.example.bmob.fragments.provost

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bmob.databinding.FragmentProvostSelectTimeBinding
import com.example.bmob.myapp.appUser
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.ProvostViewModel
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmoblibrary.base.basefragment.BaseVbFragment
import com.example.bmoblibrary.ext.showMsgShort
import com.example.bmoblibrary.ext.textString


class ProvostSelectTimeFragment : BaseVbFragment<FragmentProvostSelectTimeBinding>() {
    private val setViewModel: SetViewModel by activityViewModels()
    private val viewModel: ProvostViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        binding.provost = appUser
        binding.click = ProxyClick()
    }

    override fun createObserver() {
        viewModel.getProvostReleaseSelectTimeLiveData(appUser)
            .observe(viewLifecycleOwner) { releaseTime ->
                if (releaseTime != null) {
                    binding.releaseTimeEntity = releaseTime
                    binding.confirmBtn.visibility = View.GONE
                    binding.updateBtn.visibility = View.VISIBLE
                }
            }
    }

    inner class ProxyClick {
        fun onTimeStartLayout() {
            setViewModel.selectTime(requireContext(), "开始时间", 3, 3, 3) {
                binding.chooseStartTime.text = it
            }
        }

        fun onTimeEndLayout() {
            setViewModel.selectTime(requireContext(), "结束时间", 0, 0, 0) {
                binding.chooseEndTime.text = it
            }
        }

        fun onBack() {
            findNavController().navigateUp()
        }

        fun onConfirm() {
            binding.confirmBtn.isEnabled = false
            Log.v(LOG_TAG, "被点击了")
            val beginTime = binding.chooseStartTime.textString
            val endTime = binding.chooseEndTime.textString
            viewModel.checkIsEndValid(
                beginTime,
                endTime
            ) { isValid, message ->
                Log.v(LOG_TAG, "回调")
                if (!isValid) {
                    binding.confirmBtn.isEnabled = true
                    showMsgShort(message)
                } else {
                    //保存发布时间
                    Log.v(LOG_TAG, "日期合法")

                    viewModel.saveTime(beginTime, endTime, appUser)
                    { isSuccess, msg ->
                        showMsgShort(msg)
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

        fun onUpdate() {
            //更新时间
            val beginTime = binding.chooseStartTime.textString
            val endTime = binding.chooseEndTime.textString
            viewModel.checkIsEndValid(
                beginTime,
                endTime
            ) { isValid, message ->
                Log.v(LOG_TAG, "回调")
                if (!isValid) {
                    showMsgShort(message)
                } else {
                    //保存发布时间
                    viewModel.updateReleaseTime(
                        viewModel.getProvostReleaseSelectTimeLiveData(appUser).value!!,
                        beginTime,
                        endTime
                    ) { msg ->
                        showMsgShort(msg)
                    }
                }
            }
        }
    }
}