package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Statistic(
    val comments: Int? = 0,
    val likes: Int? = 0,
    var ratings: Int? = 0,
    var reviewsCount: Int? = 0,
    val shares: Int? = 0,
    val views: Int? = 0
) : Parcelable