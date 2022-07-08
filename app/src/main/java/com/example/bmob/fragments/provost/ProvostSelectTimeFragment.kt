package com.example.bmob.fragments.provost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentProvostHomeBinding
import com.example.bmob.databinding.FragmentProvostSelectTimeBinding


class ProvostSelectTimeFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentProvostSelectTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProvostSelectTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {

    }
}