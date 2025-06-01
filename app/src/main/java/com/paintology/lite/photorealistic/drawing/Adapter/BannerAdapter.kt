package com.paintology.lite.photorealistic.drawing.Adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.intuit.sdp.R
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Model.AppBanner
import com.paintology.lite.photorealistic.drawing.databinding.DialogBannerBinding
import com.paintology.lite.photorealistic.drawing.util.transforms.RoundedTransformation
import com.squareup.picasso.Picasso


class BannerAdapter(
    var context: Context,
    var mProductList: List<AppBanner>,
    var listener: onItemClickListener
) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    private val newList: List<AppBanner> =
        listOf(mProductList.last()) + mProductList + listOf(mProductList.first())

    interface onItemClickListener {
        fun onDialogClose(position: Int, banner: AppBanner)
        fun onItemClick(position: Int, banner: AppBanner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DialogBannerBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    fun String.copyCode() {
        try {
            val cData = ClipData.newPlainText("text", this)
            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                cData
            )
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val layoutParams = LinearLayout.LayoutParams(holder.binding.llMain.layoutParams)
            layoutParams.marginStart = R.dimen._5sdp
            layoutParams.marginEnd = R.dimen._5sdp
            holder.binding.llMain.layoutParams = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (newList[position].footer_text.isNotEmpty()) {
            holder.binding.btnGo.text = newList[position].footer_text
        }

        Picasso.get().load(Uri.parse(newList[position].image_url))
            .transform(RoundedTransformation(20, 0)).into(holder.binding.ivThumbnail)
        holder.binding.tvDialogTitle.text = newList[position].title
        holder.binding.tvDialogContent.text = newList[position].description
        holder.binding.ivClose.visibility = View.GONE
        holder.binding.ivClose.setOnClickListener {
            listener.onDialogClose(position, newList[position])
        }
        holder.binding.btnGo.setOnClickListener {
            if (newList[position].attachment_text.isNotEmpty()) {
                newList[position].attachment_text.copyCode()
            }
            listener.onItemClick(position, newList[position])
        }
        holder.binding.cvMain.onSingleClick {
            listener.onDialogClose(position,newList[position])
        }
        holder.binding.llMain.onSingleClick {
            if (newList[position].attachment_text.isNotEmpty()) {
                newList[position].attachment_text.copyCode()
            }
            listener.onItemClick(position, newList[position])
        }
    }


    override fun getItemCount(): Int {
        return newList.size
    }

    inner class ViewHolder(var binding: DialogBannerBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
