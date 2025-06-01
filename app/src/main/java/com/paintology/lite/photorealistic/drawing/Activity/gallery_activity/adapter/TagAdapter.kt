package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryTagActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ItemArtTagBinding

class TagAdapter(
    val context: Activity,
    private val artList: List<String> = mutableListOf(),
    private val isChecked: Boolean = false
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemArtTagBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapter.ViewHolder {
        return ViewHolder(ItemArtTagBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: TagAdapter.ViewHolder, position: Int) {
        holder.binding.tvArtName.text = artList[position]
        if (isChecked) {
            holder.binding.tvArtName.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.black_overlay
                )
            )
        }

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    GalleryTagActivity::class.java
                ).putExtra("searchTag", artList[holder.adapterPosition])
            )
        }
    }


}