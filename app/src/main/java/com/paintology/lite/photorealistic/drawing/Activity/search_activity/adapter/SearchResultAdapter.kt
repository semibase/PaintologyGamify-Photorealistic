package com.paintology.lite.photorealistic.drawing.Activity.search_activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.interface_event.OnSearchResultClicks
import com.paintology.lite.photorealistic.drawing.Activity.search_activity.model.SearchResultModel
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutSearchResultItemBinding

class SearchResultAdapter(
    private val onSearchResultClicks: OnSearchResultClicks,
) : ListAdapter<SearchResultModel, SearchResultAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<SearchResultModel>() {
        override fun areItemsTheSame(
            oldItem: SearchResultModel,
            newItem: SearchResultModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchResultModel,
            newItem: SearchResultModel
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutSearchResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTitle.text = item.title
            tvTutorialNo.text = item.tutorialNo.toString()
            tvTutorialContent.text = item.content
            tvNumber.text = item.peopleWatched.toString()
            Glide.with(holder.itemView.context).load(item.img).placeholder(R.drawable.feed_thumb_default).error(R.drawable.feed_thumb_default).into(imgThumbnail)
            item.rating?.let {
                toturialRatingBar.rating = it
            }
            imgMenu.onSingleClick {
                onSearchResultClicks.onMenuClick(item, position)
            }
            cardMain.onSingleClick { onSearchResultClicks.onMenuClick(item, position) }
        }
    }


    inner class ViewHolder(val binding: LayoutSearchResultItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
