package com.example.bmob.fragments.student.select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.StudentSelectThesisItemBinding


private typealias callback = (thesis:Thesis)->Unit

class StudentSelectThesisAdapter(
    private val availableThesis: MutableList<Thesis>,
    private val callback: callback
): RecyclerView.Adapter<StudentSelectThesisAdapter.StudentSelectThesisViewHolder>() {
    class StudentSelectThesisViewHolder(
        val binding: StudentSelectThesisItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup): StudentSelectThesisViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = StudentSelectThesisItemBinding.inflate(from, parent, false)
                return StudentSelectThesisViewHolder(itemBinding)
            }
        }
        fun bind(thesis:Thesis,callback: callback){
            binding.root.setOnClickListener {
                callback.invoke(thesis)
            }
            binding.thesis = thesis
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentSelectThesisViewHolder {
        return StudentSelectThesisViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: StudentSelectThesisViewHolder, position: Int) {
        holder.bind(availableThesis[position],callback)
    }

    override fun getItemCount(): Int = availableThesis.size
}
