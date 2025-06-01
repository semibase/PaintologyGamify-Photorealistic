package com.paintology.lite.photorealistic.drawing.Activity.video_intro

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.listener.onTutorialClickListener
import com.paintology.lite.photorealistic.drawing.databinding.LayoutIntroVideoBinding
import com.paintology.lite.photorealistic.drawing.util.transforms.RoundedTransformation
import com.squareup.picasso.Picasso

class IntroAdapter(
    val context: Activity,
    private val introList: List<Any?> = mutableListOf(),
    private val onTutorialClickListener: onTutorialClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutIntroVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = introList.get(position) as HashMap<*, *>
            val images = item.get("images") as HashMap<*, *>
            if (images.containsKey("thumbnail")) {
                Picasso.get().load(
                    Uri.parse(images.get("thumbnail").toString()))
                    .transform(RoundedTransformation(20, 0))
                    .into(holder.binding.ivIcon)
            }
            holder.binding.apply {
                txtTitle.text = item.get("title").toString()
            }
            holder.itemView.setOnClickListener {
                onTutorialClickListener.onTutorialClick(position, item)
            }
        }
    }

    override fun getItemCount(): Int {
        return introList.size
    }

    inner class ViewHolder(val binding: LayoutIntroVideoBinding) :
        RecyclerView.ViewHolder(binding.root)
}