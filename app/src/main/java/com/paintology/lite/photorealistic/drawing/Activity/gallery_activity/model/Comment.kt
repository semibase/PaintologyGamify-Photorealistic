package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model

import com.google.firebase.Timestamp

data class Comment(
    val id: String,
    val avatar: String,
    val comment: String,
    val country: String,
    val createdAt: Timestamp,
    val gender: String,
    val name: String,
    val userId: String,
    val parentId: String,
    var replies: List<Comment> = emptyList()
)
