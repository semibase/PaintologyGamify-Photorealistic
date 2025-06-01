package com.paintology.lite.photorealistic.drawing.challenge.model

import com.google.firebase.Timestamp

data class Comment(
    var id: String = "",
    var avatar: String = "",
    var comment: String = "",
    var created_at: Timestamp = Timestamp.now(),
    var email: String = "",
    var level: String = "",
    var name: String = "",
    var user_id: String = ""
)