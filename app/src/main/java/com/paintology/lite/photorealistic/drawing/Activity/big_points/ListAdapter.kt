package com.paintology.lite.photorealistic.drawing.Activity.big_points

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBigPointBinding
import com.paintology.lite.photorealistic.drawing.util.transforms.CircleTransform
import com.paintology.lite.photorealistic.drawing.util.transforms.RoundedTransformation
import com.squareup.picasso.Picasso
import org.json.JSONException


class ListAdapter(var context: Context, var list: List<Social>, val listener: onClickListener) :
    RecyclerView.Adapter<ListAdapter.ListHolder>() {

    class ListHolder(var binding: LayoutBigPointBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    fun interface onClickListener {
        fun onClick(position: Int, item: Social);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return ListHolder(
            LayoutBigPointBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        try {
            val social: Social = list.get(position)
            holder.binding.tvHeader.text = "Earn " + social.points.toString() + " Points!"
            holder.binding.tvTitle.text = social.title
            holder.binding.tvDesc.text = social.description
            Picasso.get().load(Uri.parse(social.image_url))
                .error(R.drawable.paintology_logo)
                .placeholder(R.drawable.paintology_logo)
                .transform(CircleTransform())
                .into(holder.binding.ivIcon)
            Picasso.get().load(Uri.parse(social.featured_image_url))
                .error(R.drawable.feed_thumb_default)
                .placeholder(R.drawable.feed_thumb_default)
                .transform(RoundedTransformation(15,0))
                .into(holder.binding.ivImage)
            holder.binding.btnSend.text = social.points.toString() + " Pts"
            holder.itemView.setOnClickListener {
                listener.onClick(holder.layoutPosition, list[holder.layoutPosition])
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
}
