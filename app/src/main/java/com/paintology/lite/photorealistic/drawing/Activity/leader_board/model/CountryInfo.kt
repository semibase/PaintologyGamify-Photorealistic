package com.paintology.lite.photorealistic.drawing.Activity.leader_board.model

import androidx.annotation.Keep

@Keep
data class CountryInfo(
    val code: String? = "",
    val name: String? = "",
    val users: Long? = 0
)
