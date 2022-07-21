package com.example.bmob.common

import androidx.recyclerview.widget.DiffUtil
import com.example.bmob.data.entity.Thesis

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