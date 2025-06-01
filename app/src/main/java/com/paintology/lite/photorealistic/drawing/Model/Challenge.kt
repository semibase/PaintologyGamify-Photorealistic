package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Challenge(
    @SerializedName("difficulty_level")
    @Expose
    var difficulty_level: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("sub_title")
    @Expose
    var sub_title: String? = null,

    @SerializedName("reward_amount")
    @Expose
    var reward_amount: String? = null,

    @SerializedName("image")
    @Expose
    var image: String? = null,

    )
