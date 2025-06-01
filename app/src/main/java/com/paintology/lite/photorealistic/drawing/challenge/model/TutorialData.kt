package com.paintology.lite.photorealistic.drawing.challenge.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TutorialData(
    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("category_id")
    @Expose
    var category_id: Int? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("images")
    @Expose
    var images: ChallengeImages? = null,
    @SerializedName("youtube")
    @Expose
    var youtube: String? = null,
    @SerializedName("guide")
    @Expose
    var guide: String? = null,
)
