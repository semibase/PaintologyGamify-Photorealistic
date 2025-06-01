package com.paintology.lite.photorealistic.drawing.Activity.profile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ItemArtBinding

class ArtAdapter(
    val context: Activity,
    private val artList: List<String> = mutableListOf(),
    private val isChecked: Boolean = false
) : RecyclerView.Adapter<ArtAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemArtBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtAdapter.ViewHolder {
        return ViewHolder(ItemArtBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtAdapter.ViewHolder, position: Int) {
        holder.binding.tvArtName.text = artList[position]
        if(isChecked)
        {
            holder.binding.tvArtName.background.setTint(ContextCompat.getColor(context,R.color.black_overlay))
        }
    }


}