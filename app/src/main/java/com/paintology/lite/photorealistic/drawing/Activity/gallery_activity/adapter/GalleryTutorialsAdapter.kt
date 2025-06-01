package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi.countDrawingView
import com.paintology.lite.photorealistic.drawing.databinding.LayoutGalleryTutorialItemBinding

class GalleryTutorialsAdapter(
    private val isFromGallery: Boolean = true,
    private val tutorialsResultList: List<NewDrawing>,
    private val onGalleryMenuClick: OnGalleryMenuClick,
    private val favourites: List<String>
) : RecyclerView.Adapter<GalleryTutorialsAdapter.ViewHolder>() {

    val array = hashMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutGalleryTutorialItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tutorialsResultList[position]
        var isFavourite = favourites.contains(item.id)

        if (!array.containsKey(item.id)) {
            array.put(item.id, item.id)
            item.id.countDrawingView()
        }

        with(holder.binding) {

            tvName.text = item.author.name

            btnDrawing.text = item.title

            tvTutorialContent.text = item.description

            toturialRatingBar.rating = item.statistic.ratings!!.toFloat()
            tvComments.text = item.statistic.comments.toString()

            if (item.links.youtube.isEmpty() || item.links.youtube.endsWith("null")) {
                ivVideo.visibility = View.GONE
            } else {
                ivVideo.visibility = View.VISIBLE
            }

            val likesFromItem = item.statistic.likes
            if (likesFromItem != null) {
                tvLikes.text = if (likesFromItem > 0) likesFromItem.toString() else "0"
            }

            Glide.with(imgThumbnail.context)
                .load(item.images.content)
                .placeholder(R.drawable.feed_thumb_default)
                .error(R.drawable.feed_thumb_default)
                .into(imgThumbnail)

            imgFavourite.isVisible = favourites.contains(item.id)

            imgMenu12.setOnClickListener {
                onGalleryMenuClick.onMenuClick(item, position, imgMenu12) {
                    isFavourite = isFavourite.not()
                    imgFavourite.isVisible = isFavourite
                }
            }

            tvName.setOnClickListener {
                onGalleryMenuClick.onNameClick(item, position)
            }

            if (isFromGallery) {
                if (position == 0) {
                    constraintLayout6.hide()
                    layoutPlus.show()
                } else {
                    layoutPlus.hide()
                    constraintLayout6.show()
                }
            } else {
                layoutPlus.hide()
            }


            cardMain1.setOnClickListener {
                onGalleryMenuClick.onItemClick(item, position)
            }

            imgThumbnail.setOnClickListener {
                onGalleryMenuClick.onItemClick(item, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return tutorialsResultList.size
    }

    inner class ViewHolder(val binding: LayoutGalleryTutorialItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnGalleryMenuClick {
        fun onMenuClick(model: NewDrawing, position: Int, root: ImageView, onFavourite: () -> Unit)

        fun onItemClick(model: NewDrawing, position: Int)
        fun onNameClick(model: NewDrawing, position: Int)

    }
}