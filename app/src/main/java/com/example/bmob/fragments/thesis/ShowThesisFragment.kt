package com.example.bmob.fragments.thesis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentShowThesisBinding

class ShowThesisFragment : Fragment(),FragmentEventListener{
    private lateinit var binding:FragmentShowThesisBinding
    private val args:ShowThesisFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowThesisBinding.inflate(inflater,container,false)
        binding.thesis = args.thesis
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
    }

    override fun setEventListener() {
        binding
    }
}