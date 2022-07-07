package com.example.bmob.fragments.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.data.entity.User
import com.example.bmob.databinding.StudentChooseThesisItemBinding
import com.example.bmob.viewmodels.SelectedModel

class SelectedAdapter(
    private val selectedModelList: MutableList<SelectedModel>
): RecyclerView.Adapter<SelectedAdapter.SearchViewHolder>() {
    class SearchViewHolder(val binding:StudentChooseThesisItemBinding):RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup): SearchViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = StudentChooseThesisItemBinding.inflate(from, parent, false)
                return SearchViewHolder(itemBinding)
            }
        }
        fun bind(selectedModel: SelectedModel){
            binding.selectedModel = selectedModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(selectedModelList[position])
    }

    override fun getItemCount(): Int = selectedModelList.size
}
