package com.example.bmob.fragments.dean.approve

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentApprovedNotApprovedBinding

/**
 * 系主任首页点击 审批课题
 */
class ApprovedNotApprovedFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentApprovedNotApprovedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApprovedNotApprovedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }
    override fun setEventListener() {

    }
}