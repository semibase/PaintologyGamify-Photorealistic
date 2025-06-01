package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TraceImage {
    @SerializedName("ID")
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

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("modified")
    @Expose
    var modified: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("sizes")
    @Expose
    var sizes: sizes? = null

}