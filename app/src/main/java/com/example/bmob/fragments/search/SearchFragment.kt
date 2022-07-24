package com.example.bmob.fragments.search

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.SearchRecyclerViewAdapter
import com.example.bmob.databinding.FragmentSearchBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.CommonHomeViewModel
import com.example.bmob.viewmodels.CommonHomeViewModel.Companion.ERROR


class SearchFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentSearchBinding
    private var adapter: SearchRecyclerViewAdapter? = null
    private val viewModel: CommonHomeViewModel by viewModels()
    private val args: SearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        //观测搜索结果
        viewModel.searchResult.observe(viewLifecycleOwner) {
            Log.v(LOG_TAG, "观测到数据：$it")
            if (it.first != ERROR) {
                Log.v(LOG_TAG, "it.first != ERROR")
                if (adapter == null) {
                    Log.v(LOG_TAG, "search adapter == null")
                    adapter = SearchRecyclerViewAdapter(it.second) { thesis ->
                        Log.v(LOG_TAG, "回调：$thesis")
                        val actionSearchFragmentToShowThesisFragment =
                            SearchFragmentDirections.actionSearchFragmentToShowThesisFragment(
                                thesis,
                                args.isShowParticipateButton
                            )
                        findNavController().navigate(actionSearchFragmentToShowThesisFragment)
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL, false
                    )
                    binding.recyclerView.adapter = adapter
                } else {
                    Log.v(LOG_TAG, "设置新list")
                    if (viewModel.nowSearch.value == it.first) {
                        adapter?.setThesisList(it.second)
                    }
                }
            } else {
                Log.v(LOG_TAG, "it.first == ERROR")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setFragment(this)
        setEventListener()
    }

    override fun setEventListener() {
        binding.backToHomeIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.searchView2.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            //点击搜索时调用
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            //此处可以设置按输入给出提示的adapter
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.v(LOG_TAG, "newText:${newText}")
                return if (!TextUtils.isEmpty(newText)) {
                    viewModel.setNowSearch(newText!!)
                    true
                } else {
                    adapter?.setThesisList(mutableListOf())
                    false
                }
            }
        })
    }
}