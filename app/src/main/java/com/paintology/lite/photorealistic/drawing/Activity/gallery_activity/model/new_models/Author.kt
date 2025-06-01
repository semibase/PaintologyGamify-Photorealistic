package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    val avatar: String = "",
    val country: String? = "",
    val level: String? = "",
    val name: String = "",
    val userId: String = ""
) : Parcelable