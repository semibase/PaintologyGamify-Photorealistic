package com.paintology.lite.photorealistic.drawing.challenge.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class QuestionMap(
    @SerializedName("correct_answer")
    @Expose
    var correct_answer: String? = null,
    @SerializedName("explanation")
    @Expose
    var explanation: String? = null,
    @SerializedName("key")
    @Expose
    var key: String? = null,
    @SerializedName("question")
    @Expose
    var question: String? = null,
)
