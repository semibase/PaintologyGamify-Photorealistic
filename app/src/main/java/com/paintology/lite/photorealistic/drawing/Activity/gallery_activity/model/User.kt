package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName


data class Result(
    val cost: Int?,
    val xp: Int?,
    val correct_answers: Int?,
    val points: Int?
) {
    // Add a no-argument constructor
    constructor() : this(null, null, null, null)
}

data class Response(
    val answer: String?,
    val correct_answer: String?,
    val key: String?
)

data class Challenge(
    val difficulty: String?,
    val total_questions: Int?,
    val limit: String?,
    val type: String?,
    val title: String?,
    val key: String?,
    val points: Int?
) {
    // Add a no-argument constructor
    constructor() : this(null, null, null, null, null, null, null)
}


data class DataModel(
    val result: Result?,
    @SerializedName("attachment")
    val attachment: Any?,
    val custom_fields: Map<String, String>?,
    val created_at: Timestamp?,
    val responses: Map<String, List<Response>>?,
    val challenge: Challenge?,
    val category: String?,
    val status: String?
)
