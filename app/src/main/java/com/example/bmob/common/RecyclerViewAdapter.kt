package com.example.bmob.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class RecyclerViewAdapter<T>(
    private val data: List<T>,
    private val bindCallback:(binding:ViewBinding,result:T)->Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.ResultViewHolder>() {
    class ResultViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            lateinit var createViewHolderCallback:(parent: ViewGroup)->ResultViewHolder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder.createViewHolderCallback.invoke(parent)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        bindCallback.invoke(holder.binding,data[position])
    }
    override fun getItemCount(): Int = data.size
}


/**
 * 使用
 *
 *         viewModel.songs.observe(viewLifecycleOwner) {
Log.v(LOG_TAG, "SearchResultFragment 观查到 songs数据")
if (currentPagerPos == SINGLE_SONG_POS) {
ResultRecyclerViewAdapter.ResultViewHolder.createViewHolderCallback = { parent->
ResultRecyclerViewAdapter.ResultViewHolder(SingleSongSearchResultBinding
.inflate(inflater,parent,false))
}
val recyclerAdapter = ResultRecyclerViewAdapter(it)
recyclerAdapter.setBindCallback { searchBinding, song->
(searchBinding as SingleSongSearchResultBinding).apply {
result = song
}
}
setRecyclerData(recyclerAdapter)
}
}
 */