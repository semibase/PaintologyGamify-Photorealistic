package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RelatedPostsDatum {
    @SerializedName("ID")
    @Expose
    var id: Int? = null

    @SerializedName("post_title")
    @Expose
    var postTitle: String? = null

    @SerializedName("post_name")
    @Expose
    var postName: String? = null

    @SerializedName("thumbImage")
    @Expose
    var thumbImage: String? = null
}