package com.paintology.lite.photorealistic.drawing.Activity.support

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ItemSupportImageBinding
import com.squareup.picasso.Picasso

class ImagesAdapter(
    val context: Activity,
    private val imagesList: List<String> = mutableListOf(),
    private val bitmapList: List<Bitmap?> = mutableListOf(),
    val onItemClick: OnItemClick
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    interface OnItemClick {
        fun onDelete(position: Int)
        fun onAdd(position: Int)

    }

    inner class ViewHolder(val binding: ItemSupportImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAdapter.ViewHolder {
        return ViewHolder(
            ItemSupportImageBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ImagesAdapter.ViewHolder, position: Int) {
        if (imagesList[position] == "") {
            holder.binding.constraintLayout6.visibility = View.GONE
            holder.binding.layoutPlus.visibility = View.VISIBLE
            holder.itemView.onSingleClick {
                onItemClick.onAdd(holder.layoutPosition)
            }
        } else {
            holder.binding.constraintLayout6.visibility = View.VISIBLE
            holder.binding.layoutPlus.visibility = View.GONE
            if (bitmapList[position] != null) {
                holder.binding.imgThumbnail.setImageBitmap(bitmapList[position])
            }else{
                Picasso.get().load(imagesList[position]).placeholder(R.drawable.img_cat_dummy)
                    .into(holder.binding.imgThumbnail)
            }
            holder.binding.btnDelete.onSingleClick {
                onItemClick.onDelete(holder.layoutPosition)
            }
        }
    }




}