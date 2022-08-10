package com.example.bmoblibrary.base.basefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.example.bmoblibrary.utils.inflateVmVbBinding

abstract class BaseVmVbFragment<VM : ViewModel, VB : ViewBinding> : BaseVmFragment<VM>() {

    /** 该类绑定的ViewDataBinding */
    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateVmVbBinding(inflater, container, false)
        return binding.root
    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}