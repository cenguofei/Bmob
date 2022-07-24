package com.example.bmob.common

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bmob.R
import com.example.bmob.data.entity.BmobBannerObject
import com.youth.banner.adapter.BannerAdapter

class BannerAdapter(
    data: List<BmobBannerObject>
) : BannerAdapter<BmobBannerObject, com.example.bmob.common.BannerAdapter.BannerViewHolder>(data) {

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView as ImageView
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerViewHolder {
        val imageView = ImageView(parent?.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return BannerViewHolder(itemView = imageView)
    }

    override fun onBindView(
        holder: BannerViewHolder?,
        data: BmobBannerObject?,
        position: Int,
        size: Int
    ) {
        Glide.with(holder!!.imageView.context)
            .load(data!!.picUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(holder.imageView)
    }
}