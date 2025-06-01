package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Metadata(
    val parentFolderPath: String = "",
    val path: String = "",
    val tutorialId: String = ""
) : Parcelable