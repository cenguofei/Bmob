package com.example.bmob.fragments.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.SearchSuggestItemBinding

class SearchRecyclerViewAdapter(
    private val callback:(thesis:Thesis)->Unit
):RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>() {
    private var thesisList:List<Thesis>? = null
    class SearchViewHolder(val binding:SearchSuggestItemBinding):RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup):SearchViewHolder{
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
        holder.bind(thesis = thesisList!![position],callback)

    }

    override fun getItemCount(): Int = thesisList!!.size

    fun setThesisListForFirst(newThesisList: List<Thesis>){
        this.thesisList = newThesisList
    }

    fun setThesisList(newThesisList: List<Thesis>){
//        this.thesisList = newThesisList
//        this.notifyDataSetChanged()
        val diffResult = DiffUtil.calculateDiff(ThesisDiffUtil(this.thesisList!!, newThesisList))
        diffResult.dispatchUpdatesTo(this)
    }
}

class ThesisDiffUtil(
    private val oldList: List<Thesis>,
    private val newList: List<Thesis>
): DiffUtil.Callback() {
    //获取旧数据元素个数
    override fun getOldListSize(): Int {
        return oldList.size
    }
    //获取新数据元素个数
    override fun getNewListSize(): Int {
        return newList.size
    }
    //是否是同一个对象
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //判断是否是同一个对象
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
    //内容是否一致
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].title == oldList[oldItemPosition].title
    }
}