package com.paintology.lite.photorealistic.drawing.Model


class NewNotificationsModel(
    val title: String?,
    val text: String?,
    val time: String?,
    val type: NewNotificationType,
    val postId: String?,
    val userId: String?,
    val name: String? = "",
    val rating: Int? = 1
)

enum class NewNotificationType {
    CHAT, FRIEND_REQ, RATING_ON_POST, COMMENT, CHALLENGE_REQ,NEW_CHALLENGE, NEW_TUTORIAL, AWARD, OTHER
}