package com.example.bmob.fragments.student.select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.StudentSelectTeacherItemBinding


class BrowseTeacherHasThesisAdapter(
    private var userList:List<User>,
    private val callback:(user: User)->Unit
): RecyclerView.Adapter<BrowseTeacherHasThesisAdapter.SearchViewHolder>() {
    class SearchViewHolder(val binding: StudentSelectTeacherItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup):SearchViewHolder{
                val from = LayoutInflater.from(parent.context)
                val itemBinding = StudentSelectTeacherItemBinding.inflate(from, parent, false)
                return SearchViewHolder(itemBinding)
            }
        }
        fun bind(user: User, callback: (thesis: User) -> Unit){
            binding.user = user
            binding.root.setOnClickListener {
                callback.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(user = userList[position],callback)
    }

    override fun getItemCount(): Int = userList.size
}
