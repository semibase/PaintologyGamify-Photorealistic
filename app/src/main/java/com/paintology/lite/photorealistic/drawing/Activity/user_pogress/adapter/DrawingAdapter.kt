package com.paintology.lite.photorealistic.drawing.Activity.user_pogress.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.listener.onTutorialClickListener
import com.paintology.lite.photorealistic.drawing.databinding.LayoutUserDrawingBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutViewAllBinding
import com.squareup.picasso.Picasso

class DrawingAdapter(
    val context: Activity,
    private val drawingsList: List<Any?> = mutableListOf(),
    private val onTutorialClickListener: onTutorialClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view =
                LayoutUserDrawingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewHolder(view)
        } else {
            val view =
                LayoutViewAllBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewAllHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = drawingsList.get(position) as HashMap<*, *>
            val images = item.get("images") as HashMap<*, *>
            if (images.containsKey("content")) {
                Picasso.get().load(Uri.parse(images["content"].toString()))
                    .into(holder.binding.imgThumbnail)
            }
            val stats = item.get("statistic") as HashMap<*, *>
            holder.binding.apply {
                btnDrawing.text = item.get("title").toString()
                tvTutorialContent.text = item.get("description").toString()
                tvLikes.text = stats.get("likes").toString()
                tvComments.text = stats.get("comments").toString()
            }
            holder.itemView.setOnClickListener {
                onTutorialClickListener.onTutorialClick(position, item)
            }
        } else {
            holder.itemView.setOnClickListener {
                context.startActivity(Intent(context, GalleryActivity::class.java))
            }
        }
    }

    override fun getItemCount(): Int {
        return drawingsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (drawingsList.get(position) == null) 1 else 0
    }

    inner class ViewHolder(val binding: LayoutUserDrawingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewAllHolder(val binding: LayoutViewAllBinding) :
        RecyclerView.ViewHolder(binding.root)
}