package com.example.bmob.fragments.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.StudentChooseThesisItemBinding

class SelectedAdapter: RecyclerView.Adapter<SelectedAdapter.SearchViewHolder>() {
    private var thesisList:List<Thesis>? = null
    private var userList:List<User>? = null
    class SearchViewHolder(val binding:StudentChooseThesisItemBinding):RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup): SearchViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = StudentChooseThesisItemBinding.inflate(from, parent, false)
                return SearchViewHolder(itemBinding)
            }
        }
        fun bind(user: User,thesis:Thesis){
            binding.user = user
            binding.thesis = thesis
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(user = userList!![position], thesis = thesisList!![position])
    }

    override fun getItemCount(): Int = userList!!.size

    fun setThesisListForFirst(newThesisList: List<Thesis>){
        this.thesisList = newThesisList
        this.notifyDataSetChanged()
    }
}
