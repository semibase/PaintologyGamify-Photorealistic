package com.paintology.lite.photorealistic.drawing.Activity.leader_board.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.LeaderBoardRankingModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutLeaderBoardItemsBinding
import com.paintology.lite.photorealistic.drawing.util.FireUtils
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import com.paintology.lite.photorealistic.drawing.util.sendUserEventWithParam

class LeaderBoardRankingAdapter(
    private val leaderBoardRankList: List<LeaderBoardRankingModel> = mutableListOf(),
    private val country: String?,
    val onCurrentUserProfileMatch: (String) -> Unit
) : RecyclerView.Adapter<LeaderBoardRankingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutLeaderBoardItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = leaderBoardRankList[position]
        with(holder.binding) {
            val ct = holder.binding.root.context
            item.avatar?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .placeholder(R.drawable.img_default_avatar)  // Placeholder image
                    .into(imgProfile)
            }
            val pos = position.plus(4).toString()
            item.getUserBadgeIcon()?.let { imgChallengeBadge.setImageResource(it) }
            tvName.text = item.name
            tvTotalAwards.text = "${item.level}"
            tvTotalPoints.text = "${item.points} Pts"
            tvSerialNumber.text = pos

            when (item.level) {
                "Expert" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_expert_)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Advanced 3" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_ad_3)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Advanced 2" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_ad_2)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Advanced 1" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_ad_1)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Intermediate 3" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_inter_3)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Intermediate 2" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_inter_2)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Intermediate 1" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_inter_1)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Beginner 3" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_big_3)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Beginner 2" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_big_2)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                "Beginner 1" -> {
                    layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board_big_1)
                    tvName.setTextColor(Color.parseColor("#000000"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                    onCurrentUserProfileMatch(pos)
                }

                else -> {
                    layoutMain.setBackgroundResource(0)
                    tvName.setTextColor(Color.parseColor("#111111"))
                    tvTotalPoints.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }

            if (FirebaseUtils.isYOU(ct, item.docId)) {
                layoutMain.setBackgroundResource(R.drawable.bg_selected_leader_board)
                tvName.setTextColor(Color.parseColor("#ffffff"))
                tvSerialNumber.setTextColor(Color.parseColor("#ffffff"))
                tvTotalPoints.setTextColor(Color.parseColor("#ffffff"))
                onCurrentUserProfileMatch(pos)
            }

            holder.binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("rank", holder.layoutPosition.plus(4).toString())
                if (country == null) {
                    bundle.putString("country", "")
                } else {
                    bundle.putString("country", country)
                }
                bundle.putString("user_id", item.docId)
                ct.sendUserEventWithParam(StringConstants.leaderboards_open_user, bundle)
                FireUtils.openProfileScreen(ct, item.docId.toString())
            }
        }

    }

    override fun getItemCount(): Int {
        return leaderBoardRankList.size
    }

    inner class ViewHolder(val binding: LayoutLeaderBoardItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}