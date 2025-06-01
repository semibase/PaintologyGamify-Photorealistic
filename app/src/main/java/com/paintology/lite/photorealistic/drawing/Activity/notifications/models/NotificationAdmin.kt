package com.paintology.lite.photorealistic.drawing.Activity.notifications.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationAdmin(
    var id: String = "",
    val body: String = "",
    val created_at: Timestamp? = null,
    val data: Data = Data(),
    val image_url: String = "",
    val push: Boolean = false,
    val read: Boolean = false,
    val readFromApp: Boolean = false,
    val readFromDevice: Boolean = false,
    val title: String = "",
    val topic: String = "",
) : Parcelable