package com.example.bmob.fragments.mine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmob.R
import com.example.bmob.databinding.FragmentMineBinding


class MineFragment : Fragment() {
    private lateinit var binding:FragmentMineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater,container,false)
        return binding.root
    }
}