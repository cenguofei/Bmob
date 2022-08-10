package com.example.bmob.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.ApproveThesisItemBinding
import com.example.bmob.databinding.ItemApprovedNotApprovedThesisBinding

class ApproveThesisAdapter(
    private var data: MutableList<MutableList<Thesis>>,
    private val teacherThesisOnClickCallback: (teacherThesis: Thesis) -> Unit
) : RecyclerView.Adapter<ApproveThesisAdapter.ApproveThesisAdapterViewHolder>() {

    class ApproveThesisAdapterViewHolder(val binding: ApproveThesisItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun createViewHolder(parent: ViewGroup): ApproveThesisAdapterViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = ApproveThesisItemBinding.inflate(from, parent, false)
                return ApproveThesisAdapterViewHolder(itemBinding)
            }
        }

        fun bind(
            holder: ApproveThesisAdapterViewHolder,
            thesisList: MutableList<Thesis>,
            teacherThesisOnClickCallback: (teacherThesis: Thesis) -> Unit,
            parent: ViewGroup
        ) {
            holder.binding.thesisTeacherName = thesisList[0].teacherName
            thesisList.forEach { thesis ->
                val from = LayoutInflater.from(parent.context)
                val itemBinding =
                    ItemApprovedNotApprovedThesisBinding.inflate(from, parent, false)
                itemBinding.tile = thesis.title
                itemBinding.desc = thesis.description
                itemBinding.root.setOnClickListener {
                    teacherThesisOnClickCallback.invoke(thesis)
                }
                holder.binding.itemContainer.addView(itemBinding.root)
            }
            holder.binding.itemContainer
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ApproveThesisAdapterViewHolder {
        return ApproveThesisAdapterViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ApproveThesisAdapterViewHolder, position: Int) {
        holder.bind(
            holder,
            data[position],
            teacherThesisOnClickCallback,
            holder.binding.rootLinearLayout
        )

    }

    override fun getItemCount(): Int = data.size
}
