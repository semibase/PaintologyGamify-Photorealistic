package com.paintology.lite.photorealistic.drawing.challenge.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GalleryModel(
    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("imageUrl")
    @Expose
    var imageUrl: String? = null,
    @SerializedName("tags")
    @Expose
    var tags: String? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("uid")
    @Expose
    var uid: String? = null,
)
