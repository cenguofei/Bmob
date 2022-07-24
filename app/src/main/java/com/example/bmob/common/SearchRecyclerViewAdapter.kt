package com.example.bmob.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.SearchSuggestItemBinding

class SearchRecyclerViewAdapter(
    var data: MutableList<Thesis>?,
    private val callback: (thesis: Thesis) -> Unit
) : RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>() {
    class SearchViewHolder(val binding: SearchSuggestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun createViewHolder(parent: ViewGroup): SearchViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = SearchSuggestItemBinding.inflate(from, parent, false)
                return SearchViewHolder(itemBinding)
            }
        }

        fun bind(thesis: Thesis, callback: (thesis: Thesis) -> Unit) {
            binding.thesis = thesis
            binding.root.setOnClickListener {
                callback.invoke(thesis)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(thesis = data!![position], callback)

    }

    override fun getItemCount(): Int = data!!.size


    fun setThesisList(newThesisList: MutableList<Thesis>) {
        if (this.data != null) {
            val diffResult = DiffUtil.calculateDiff(ThesisDiffUtil(this.data!!, newThesisList))
            this.data = newThesisList
            diffResult.dispatchUpdatesTo(this)
        }
    }
}