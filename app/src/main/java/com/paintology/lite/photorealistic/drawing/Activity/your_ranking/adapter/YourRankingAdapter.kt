package com.paintology.lite.photorealistic.drawing.Activity.your_ranking.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.show
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutYourRankingBinding

class YourRankingAdapter(
    private val yourRankingList: List<YourRankingModel> = mutableListOf(),
    val rank: String? = "",
    private val onCountryClick: OnCountryClick,
) : RecyclerView.Adapter<YourRankingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutYourRankingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = yourRankingList[position]
        with(holder.binding) {
            tvSerialNo.text = item.serialNo.toString()
            item.imgRankLevel?.let { imgRank.setImageResource(it) }
            tvRankLevel.text = item.tvRankLevel
            tvTotalUsers.text = "${item.tvTotalUsers} Users"

            if (rank != null && rank.lowercase() == tvRankLevel.text.toString().lowercase()) {
                cardMainLayout.setBackgroundResource(R.drawable.bg_selected_leader_board)
                tvYourRank.show()
                tvTotalUsers.setTextColor(Color.parseColor("#ffffff"))
                tvRankLevel.setTextColor(Color.parseColor("#ffffff"))
            } else {
                cardMainLayout.setBackgroundResource(R.drawable.bg_search)
                tvYourRank.hide()
                tvTotalUsers.setTextColor(Color.parseColor("#9B9B9B"))
                tvRankLevel.setTextColor(Color.parseColor("#000000"))
            }
            cardMainLayout.onSingleClick {
                onCountryClick.countryClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return yourRankingList.size
    }

    inner class ViewHolder(val binding: LayoutYourRankingBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnCountryClick {
        fun countryClick(model: YourRankingModel)
    }
}