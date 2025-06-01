package com.paintology.lite.photorealistic.drawing.Model

import com.paintology.lite.photorealistic.drawing.util.NotificationType

data class NotificationModel(
    val title: String?,
    val text: String?,
    val type: NotificationType,
    val postId: String?,
    val userId: String?
)
