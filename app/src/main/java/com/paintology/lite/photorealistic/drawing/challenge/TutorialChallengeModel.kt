package com.paintology.lite.photorealistic.drawing.challenge

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.paintology.lite.photorealistic.drawing.challenge.model.ChallengeImages
import com.paintology.lite.photorealistic.drawing.challenge.model.Comment
import com.paintology.lite.photorealistic.drawing.challenge.model.CustomFields
import com.paintology.lite.photorealistic.drawing.challenge.model.Statistic

data class TutorialChallengeMode(


    @SerializedName("images")
    @Expose
    var images: ChallengeImages? = null,
    @SerializedName("cost")
    @Expose
    var cost: Long? = null,
    @SerializedName("active")
    @Expose
    var active: Boolean? = null,
    @SerializedName("createdAt")
    @Expose
    var createdAt: com.google.firebase.Timestamp? = null,
    @SerializedName("description")
    @Expose
    var description: String? =null,
    @SerializedName("type")
    @Expose
    var type: String? =null,
    @SerializedName("title")
    @Expose
    var title: String? =null,
    @SerializedName("points")
    @Expose
    var points: Long? =null,
    @SerializedName("tutorialId")
    @Expose
    var tutorialId: String? =null,
    @SerializedName("difficulty")
    @Expose
    var difficulty: String? =null,
    @SerializedName("updatedAt")
    @Expose
    var updatedAt: com.google.firebase.Timestamp? =null,
    @SerializedName("xp")
    @Expose
    var xp: Long? =null,
    @SerializedName("limit")
    @Expose
    var limit: String? =null,
    @SerializedName("key")
    @Expose
    var key: String? = null,
    @SerializedName("custom_fields")
    @Expose
    var custom_fields: CustomFields? = null,
    var statistic: Statistic = Statistic(),
    var comments: MutableList<Comment> = mutableListOf(),
    var likes: MutableList<Comment> = mutableListOf(),
)


