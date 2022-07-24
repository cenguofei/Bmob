package com.example.bmob.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class RecyclerViewAdapter<T>(
    private var data: List<T>,
    private val bindCallback: (binding: ViewBinding, result: T) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.ResultViewHolder>() {
    class ResultViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            lateinit var createViewHolderCallback: (parent: ViewGroup) -> ResultViewHolder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder.createViewHolderCallback.invoke(parent)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        bindCallback.invoke(holder.binding, data[position])
    }

    override fun getItemCount(): Int = data.size
}