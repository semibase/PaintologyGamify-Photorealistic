package com.paintology.lite.photorealistic.drawing.Activity.notifications.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(
    val target_id: String = "",
    val target_name: String = "",
    val target_type: String = "",
    val target_user_name: String = ""
) : Parcelable