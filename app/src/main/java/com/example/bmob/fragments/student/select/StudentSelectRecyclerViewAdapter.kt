package com.example.bmob.fragments.student.select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.StudentSelectTeacherItemBinding


class StudentSelectRecyclerViewAdapter(
    private var userList:List<Thesis>,
    private val callback:(thesis: Thesis)->Unit
): RecyclerView.Adapter<StudentSelectRecyclerViewAdapter.SearchViewHolder>() {
    class SearchViewHolder(val binding: StudentSelectTeacherItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup):SearchViewHolder{
                val from = LayoutInflater.from(parent.context)
                val itemBinding = StudentSelectTeacherItemBinding.inflate(from, parent, false)
                return SearchViewHolder(itemBinding)
            }
        }
        fun bind(thesis: Thesis, callback: (thesis: Thesis) -> Unit){

//            binding.root.setOnClickListener {
//                callback.invoke(thesis)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(thesis = userList[position],callback)
    }

    override fun getItemCount(): Int = userList.size
}
