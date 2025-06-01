package com.paintology.lite.photorealistic.drawing.Activity.user_pogress.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.listener.onTutorialClickListener
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.DashboardScreen.CategoryActivity
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialcategory
import com.paintology.lite.photorealistic.drawing.databinding.LayoutSeeAllBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutVerticalTutorialBinding
import com.squareup.picasso.Picasso

class TutorialAdapter(
    val context: Activity,
    val level: String,
    private val tutorialList: List<Any?> = mutableListOf(),
    private val onTutorialClickListener: onTutorialClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view =
                LayoutVerticalTutorialBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewHolder(view)
        } else {
            val view =
                LayoutSeeAllBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewAllHolder(view)
        }
    }

    fun gotoUrl(url: String?) {
        try {
            val viewIntent =
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(url)
                )
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isContainsQuora(tutorialcategories: List<Tutorialcategory>): Boolean {
        var isCheck = false
        for (i in tutorialcategories.indices) {
            if (tutorialcategories[i].name.equals("quora", ignoreCase = true)) {
                isCheck = true
                break
            }
        }
        return isCheck
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = tutorialList.get(position) as HashMap<*, *>
            val images = item.get("images") as HashMap<*, *>
            if (images.containsKey("thumbnail")) {
                Picasso.get().load(Uri.parse(images.get("thumbnail").toString()))
                    .into(holder.binding.ivTutorialCategory)
            }

            // Extract the YouTube link
            val links = item.get("links") as HashMap<*, *>
            val redirect = links["redirect"] ?: ""
            val external = links["external"] ?: ""

            val mTutorialcategories = ArrayList<Tutorialcategory>()
            try {
                val mlistCategory = item.get("categories") as List<java.util.HashMap<String, Any>>
                for (j in mlistCategory.indices) {
                    val mTutorialcategory = Tutorialcategory()
                    mTutorialcategory.thumbnail = mlistCategory[j]["thumbnail"].toString()
                    mTutorialcategory.id = mlistCategory[j]["id"].toString()
                    mTutorialcategory.name = mlistCategory[j]["name"].toString()
                    mTutorialcategories.add(mTutorialcategory)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            holder.binding.apply {
                tvCategory.text = item.get("title").toString().split(" - ")[0]
                tvCategoryDesc.text = item.get("title").toString()
                tvId.text = item.get("id").toString()
            }

            holder.binding.ivMore.visibility = View.GONE

            holder.binding.tvCategory.setText(item.get("title").toString())
            holder.binding.tvCategoryDesc.setText(item.get("content").toString())

            if (!redirect.toString().equals("", ignoreCase = true)) {
                holder.binding.ivLink.setVisibility(View.VISIBLE)
                holder.binding.ivLink.setOnClickListener(View.OnClickListener { v: View? ->
                    gotoUrl(redirect.toString())
                })
            } else {
                holder.binding.ivLink.setVisibility(View.GONE)
            }



            holder.itemView.setOnClickListener {
                onTutorialClickListener.onTutorialClick(position, item)
            }
        } else {
            holder.itemView.setOnClickListener {
                context.openActivity(CategoryActivity::class.java)
            }
        }
    }

    override fun getItemCount(): Int {
        return tutorialList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (tutorialList.get(position) == null) 1 else 0

    }

    inner class ViewHolder(val binding: LayoutVerticalTutorialBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewAllHolder(val binding: LayoutSeeAllBinding) :
        RecyclerView.ViewHolder(binding.root)
}