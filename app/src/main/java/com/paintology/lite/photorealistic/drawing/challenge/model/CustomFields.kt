package com.paintology.lite.photorealistic.drawing.challenge.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.paintology.lite.photorealistic.drawing.challenge.ChallengeResource

data class CustomFields(
    @SerializedName("category")
    @Expose
    var category: Category? = null,
    @SerializedName("tutorial_data")
    @Expose
    var tutorial_data: TutorialData? = null,
    var variant: String? = null,
    var links: MutableList<ChallengeResource> = mutableListOf(),
    var description: String = "",
    var difficulty: String = "",
    var quiz_type: String = "",
    var questions: MutableList<Question> = mutableListOf()
)