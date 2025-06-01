package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewDrawing(
    val author: Author = Author(),
    val createdAt: String = "",
    val description: String = "",
    var id: String = "",
    val images: Images = Images(),
    val links: Links = Links(),
    val metadata: Metadata = Metadata(),
    val referenceId: String = "",
    val statistic: Statistic = Statistic(),
    val tags: List<String> = listOf(),
    val title: String = "",
    val type: String = ""
) : Parcelable
