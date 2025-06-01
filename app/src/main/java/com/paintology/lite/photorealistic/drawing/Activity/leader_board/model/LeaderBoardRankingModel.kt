package com.paintology.lite.photorealistic.drawing.Activity.leader_board.model

import com.paintology.lite.photorealistic.drawing.R

data class LeaderBoardRankingModel(
    val docId: String?,
    val externalId: String?,
    val avatar: String?,
    val name: String?,
    val awards: Int?,
    val points: Int?,
    val level: String?
) {
    fun getUserBadgeIcon(): Int? {
        return when (level) {
            "Beginner 1" -> R.drawable.img_beginner_1
            "Beginner 2" -> R.drawable.img_beginner_2
            "Beginner 3" -> R.drawable.img_beginner_3
            "Intermediate 1" -> R.drawable.img_intermidiate_1
            "Intermediate 2" -> R.drawable.img_intermidiate_2
            "Intermediate 3" -> R.drawable.img_intermidiate_3
            "Advanced 1" -> R.drawable.img_advance_1
            "Advanced 2" -> R.drawable.img_advance_2
            "Advanced 3" -> R.drawable.img_advance_3
            else -> null
        }
    }

    companion object {
        fun getUserBadgeIcon(level: String): Int? {
            return when (level) {
                "Beginner 1" -> R.drawable.img_beginner_1
                "Beginner 2" -> R.drawable.img_beginner_2
                "Beginner 3" -> R.drawable.img_beginner_3
                "Intermediate 1" -> R.drawable.img_intermidiate_1
                "Intermediate 2" -> R.drawable.img_intermidiate_2
                "Intermediate 3" -> R.drawable.img_intermidiate_3
                "Advanced 1" -> R.drawable.img_advance_1
                "Advanced 2" -> R.drawable.img_advance_2
                "Advanced 3" -> R.drawable.img_advance_3
                else -> null
            }
        }

        fun getUserBG(level: String): Int? {
            return when (level) {
                "Beginner 1" -> R.drawable.bg_selected_leader_board_big_1
                "Beginner 2" -> R.drawable.bg_selected_leader_board_big_2
                "Beginner 3" -> R.drawable.bg_selected_leader_board_big_3
                "Intermediate 1" -> R.drawable.bg_selected_leader_board_inter_1
                "Intermediate 2" -> R.drawable.bg_selected_leader_board_inter_2
                "Intermediate 3" -> R.drawable.bg_selected_leader_board_inter_3
                "Advanced 1" -> R.drawable.bg_selected_leader_board_ad_1
                "Advanced 2" -> R.drawable.bg_selected_leader_board_ad_2
                "Advanced 3" -> R.drawable.bg_selected_leader_board_ad_3
                "Expert" -> R.drawable.bg_selected_leader_board_expert_
                else -> null
            }
        }
    }
}