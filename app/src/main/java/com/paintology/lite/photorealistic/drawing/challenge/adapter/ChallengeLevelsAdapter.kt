package com.paintology.lite.photorealistic.drawing.challenge.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.Count
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.challenge.ChallengeLevelsModel
import com.paintology.lite.photorealistic.drawing.databinding.LayoutChallengeLevelBinding

class ChallengeLevelsAdapter(
    private val counts: List<Count>,
    private val challengeList: MutableList<ChallengeLevelsModel> = mutableListOf(),
    val userCurrentLevel: String,
    private val levelClick: LevelClick,
) : RecyclerView.Adapter<ChallengeLevelsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutChallengeLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = challengeList[position]
        with(holder.binding) {


            item.imgChallenge?.let { imgChallengeLevel.setImageResource(it) }
            tvChallengeTitle.text = item.titleChallenge


            // Find the count that matches the challenge title
            val matchingCount = counts.find { it.value == item.titleChallenge }
            if (matchingCount != null) {

                if (matchingCount.count <= 0) {
                    holder.binding.tvTotalDrawings.visibility = View.INVISIBLE
                } else {
                    holder.binding.tvTotalDrawings.text = " ( ${matchingCount.count} Drawing )"

                    holder.binding.tvTotalDrawings.visibility = View.VISIBLE
                }



//                tvChallengeContent.text = "${matchingCount.count} users are participating in this challenge"
            }
//            else {
////                tvChallengeContent.text = "0 users are participating in this challenge"
//            }

            if (item.titleChallenge.equals(userCurrentLevel)) {
                layoutMainLevel.setBackgroundResource(R.drawable.bg_selected_leader_board)
            }

            layoutMainLevel.onSingleClick {
                levelClick.onCLick(position, item)
            }


        }
    }

    override fun getItemCount(): Int {
        return challengeList.size
    }

    inner class ViewHolder(val binding: LayoutChallengeLevelBinding) :
        RecyclerView.ViewHolder(binding.root)
}

interface LevelClick {
    fun onCLick(position: Int, model: ChallengeLevelsModel)
}