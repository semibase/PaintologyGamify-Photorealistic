package com.paintology.lite.photorealistic.drawing.Activity.user_pogress.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.listener.onTutorialClickListener
import com.paintology.lite.photorealistic.drawing.databinding.LayoutUserTutorialBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutViewAllBinding
import com.paintology.lite.photorealistic.drawing.room.entities.SavedDrawingEntity
import com.squareup.picasso.Picasso
import java.io.File

class UserTutorialAdapter(
    val context: Activity,
    private val tutorialList: List<Any?> = mutableListOf(),
    private val userProgress: HashMap<String, Int>,
    private val onTutorialClickListener: onTutorialClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 0) {
            val view =
                LayoutUserTutorialBinding.inflate(
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = tutorialList.get(position) as SavedDrawingEntity
            Picasso.get().load(File(item.localPath))
                .into(holder.binding.imgThumbnail)

            holder.binding.apply {
                if (userProgress.containsKey(item.postId.toString())) {
                    seekbarTutorial.progress = userProgress.get(item.postId.toString())!!
                    tvProgress.text =
                        "${userProgress.get(item.postId.toString())} / ${seekbarTutorial.max}"
                }
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
        return tutorialList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (tutorialList.get(position) == null) 1 else 0

    }

    inner class ViewHolder(val binding: LayoutUserTutorialBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewAllHolder(val binding: LayoutViewAllBinding) :
        RecyclerView.ViewHolder(binding.root)
}