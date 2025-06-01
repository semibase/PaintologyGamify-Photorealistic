package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OverlayImage {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("filename")
    @Expose
    var filename: String? = null

    @SerializedName("filesize")
    @Expose
    var filesize: Int? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("link")
    @Expose
    var link: String? = null

    @SerializedName("alt")
    @Expose
    var alt: String? = null

    @SerializedName("author")
    @Expose
    var author: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("caption")
    @Expose
    var caption: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("uploaded_to")
    @Expose
    var uploadedTo: Int? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("modified")
    @Expose
    var modified: String? = null

    @SerializedName("menu_order")
    @Expose
    var menuOrder: Int? = null

    @SerializedName("mime_type")
    @Expose
    var mimeType: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("subtype")
    @Expose
    var subtype: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null

    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("sizes")
    @Expose
    var sizes: OverlayImageSizes? = null
}