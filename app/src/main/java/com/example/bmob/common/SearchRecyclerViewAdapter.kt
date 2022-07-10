package com.example.bmob.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.SearchSuggestItemBinding
import com.example.bmob.utils.LOG_TAG

class SearchRecyclerViewAdapter(
    var data:MutableList<Thesis>?,
    private val callback:(thesis:Thesis)->Unit
):RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>() {
    class SearchViewHolder(val binding:SearchSuggestItemBinding):RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup): SearchViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = SearchSuggestItemBinding.inflate(from, parent, false)
                return SearchViewHolder(itemBinding)
            }
        }
        fun bind(thesis:Thesis,callback: (thesis:Thesis) -> Unit){
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
        holder.bind(thesis = data!![position],callback)

    }

    override fun getItemCount(): Int = data!!.size


    fun setThesisList(newThesisList: MutableList<Thesis>){
        if (this.data != null){
            val diffResult = DiffUtil.calculateDiff(ThesisDiffUtil(this.data!!, newThesisList))
            diffResult.dispatchUpdatesTo(this)
        }else{
            Log.v(LOG_TAG,"data=null")
        }
    }
}

class ThesisDiffUtil(
    private val oldList: MutableList<Thesis>,
    private val newList: MutableList<Thesis>
): DiffUtil.Callback() {
    //获取旧数据元素个数
    override fun getOldListSize(): Int {
        Log.v(LOG_TAG,"oldList.size=${oldList.size}")
        return oldList.size
    }
    //获取新数据元素个数
    override fun getNewListSize(): Int {
        Log.v(LOG_TAG,"newList.size=${newList.size}")
        return newList.size
    }
    //是否是同一个对象
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //判断是否是同一个对象
        Log.v(LOG_TAG,"newList[newItemPosition] == oldList[oldItemPosition]  ${newList[newItemPosition] == oldList[oldItemPosition]}")
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
    //内容是否一致
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.v(LOG_TAG,"newList[newItemPosition].title == oldList[oldItemPosition].title ${newList[newItemPosition].title == oldList[oldItemPosition].title}")
        return newList[newItemPosition].title == oldList[oldItemPosition].title
    }
}