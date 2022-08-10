package com.example.bmoblibrary.base.basefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.bmoblibrary.base.IBase
import com.example.bmoblibrary.utils.inflateVbBinding

open class BaseVbFragment<VB:ViewBinding>:Fragment(),IBase {
    /** 该类绑定的ViewDataBinding */
    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    override fun setEventListener(){}

    override fun createObserver(){}

    override fun initView(savedInstanceState: Bundle?){}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateVbBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        createObserver()
        setEventListener()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}