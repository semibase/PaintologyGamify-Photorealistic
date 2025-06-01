package com.paintology.lite.photorealistic.drawing.DashboardScreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.LayoutYourRankingBinding
import com.paintology.lite.photorealistic.drawing.gallery.Interface_select_item
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.show


class LevelRankingAdapter (
    private val yourRankingList: List<YourRankingModel> = mutableListOf(),
    private val interfaceItem : Interface_select_item,
    private val selectedPos : Int = 0
) : RecyclerView.Adapter<LevelRankingAdapter.ViewHolder>() {

   // var selectedPos = 0

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
            tvTotalUsers.text = "${item.tvTotalUsers} Tutorials"
            imgContributers.visibility = View.INVISIBLE
            tvTotalUsers.visibility = View.VISIBLE

            if (position == selectedPos){
                cardMainLayout.setBackgroundResource(R.drawable.bg_selected_leader_board)
                tvYourRank.show()
                tvTotalUsers.setTextColor(Color.parseColor("#ffffff"));
                tvRankLevel.setTextColor(Color.parseColor("#ffffff"));
            }else{
                cardMainLayout.setBackgroundResource(R.drawable.bg_search)
                 tvYourRank.hide()
                tvTotalUsers.setTextColor(Color.parseColor("#000000"));
                tvRankLevel.setTextColor(Color.parseColor("#000000"));
            }

            cardMainLayout.setOnClickListener {
               // selectedPos = position
                notifyDataSetChanged()
                interfaceItem.onSubMenuClick(null,null,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return yourRankingList.size
    }

    inner class ViewHolder(val binding: LayoutYourRankingBinding) :
        RecyclerView.ViewHolder(binding.root)
}