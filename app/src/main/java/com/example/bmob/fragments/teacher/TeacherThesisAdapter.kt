package com.example.bmob.fragments.teacher

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.common.ThesisDiffUtil
import com.example.bmob.data.entity.Thesis
import com.example.bmob.databinding.ItemTeacherReleaseBinding
import com.example.bmob.utils.LOG_TAG

class TeacherThesisAdapter(
    private val callback:(thesis: Thesis)->Unit
): RecyclerView.Adapter<TeacherThesisAdapter.ThesisViewHolder>() {
    private var thesisList:List<Thesis>? = null
    class ThesisViewHolder(val binding:ItemTeacherReleaseBinding):RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun createViewHolder(parent: ViewGroup): ThesisViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemBinding = ItemTeacherReleaseBinding.inflate(from, parent, false)
                return ThesisViewHolder(itemBinding)
            }
        }
        fun bind(thesis:Thesis,callback: (thesis:Thesis) -> Unit){
            binding.thesis = thesis
            binding.root.setOnClickListener {
                callback.invoke(thesis)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThesisViewHolder {
        return ThesisViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ThesisViewHolder, position: Int) {
        holder.bind(thesis = thesisList!![position],callback)
    }

    override fun getItemCount(): Int = thesisList!!.size

    fun setThesisListForFirst(newThesisList: List<Thesis>){
        this.thesisList = newThesisList
        this.notifyDataSetChanged()

        Log.v(LOG_TAG,"setThesisListForFirst:$newThesisList")
    }

    fun setDiffThesisList(newThesisList: List<Thesis>){
        Log.v(LOG_TAG,"setDiffThesisList:$newThesisList")
        val diffResult = DiffUtil.calculateDiff(ThesisDiffUtil(this.thesisList!!, newThesisList))
        diffResult.dispatchUpdatesTo(this)
        diffResult.convertNewPositionToOld(0)
        Log.v(LOG_TAG,"diffResult分配完成")
    }
}