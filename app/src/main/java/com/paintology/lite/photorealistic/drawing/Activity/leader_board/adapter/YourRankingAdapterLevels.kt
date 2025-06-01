package com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutYourRankingLevelsBinding

class YourRankingAdapterLevels(
    private val yourRankingList: List<YourRankingModel> = mutableListOf(),
    private val onCountryClick: OnCountryClick
) : RecyclerView.Adapter<YourRankingAdapterLevels.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutYourRankingLevelsBinding.inflate(
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

            when (item.tvRankLevel) {
                "Expert" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_expert_)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Advanced 3" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_ad_3)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Advanced 2" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_ad_2)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Advanced 1" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_ad_1)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Intermediate 3" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_inter_3)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Intermediate 2" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_inter_2)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Intermediate 1" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_inter_1)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Beginner 3" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_big_3)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Beginner 2" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_big_2)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                "Beginner 1" -> {
                    clColor.setBackgroundResource(R.drawable.bg_selected_leader_board_big_1)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }

                else -> {
                    clColor.setBackgroundResource(R.drawable.bg_search)
                    tvYourRank.hide()
                    tvRankLevel.setTextColor(Color.parseColor("#000000"))
                }
            }

            cardMainLayout.onSingleClick {
                onCountryClick.countryClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return yourRankingList.size
    }
    inner class ViewHolder(val binding: LayoutYourRankingLevelsBinding) : RecyclerView.ViewHolder(binding.root)
    interface OnCountryClick {
        fun countryClick(model: YourRankingModel)
    }
}