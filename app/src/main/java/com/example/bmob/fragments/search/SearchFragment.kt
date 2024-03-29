package com.example.bmob.fragments.search

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.SearchRecyclerViewAdapter
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.FragmentSearchBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.SearchViewModel
import com.example.bmob.viewmodels.SearchViewModel.Companion.ERROR
import com.example.bmoblibrary.base.basefragment.BaseFragment


class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
    private var adapter: SearchRecyclerViewAdapter? = null
    private val args: SearchFragmentArgs by navArgs()

    override fun createObserver() {
        //观测搜索结果
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.first != ERROR) {
                if (adapter == null) {
                    initAdapter(it.second)
                } else {
                    if (viewModel.nowSearch.value == it.first) {
                        adapter?.setThesisList(it.second)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setFragment(this)
    }

    private fun initAdapter(thesisList: MutableList<Thesis>) {
        adapter = SearchRecyclerViewAdapter(thesisList) { thesis ->
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