package com.paintology.lite.photorealistic.drawing.Activity.notifications.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    var id: String = "",
    val body: String = "",
    val created_at: Timestamp? = null,
    val push: Boolean = false,
    var readFromApp: Boolean = false,
    val readFromDevice: Boolean = false,
    var read: Boolean = false,
    val delete: Boolean = false,
    val title: String = "",
    val image_url: String? = null,
    val data: NotificationData = NotificationData(),
    var isAccepted: Boolean = false
) : Parcelable