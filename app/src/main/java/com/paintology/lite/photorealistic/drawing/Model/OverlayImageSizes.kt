package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OverlayImageSizes {
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null

    @SerializedName("thumbnail-width")
    @Expose
    var thumbnailWidth: Int? = null

    @SerializedName("thumbnail-height")
    @Expose
    var thumbnailHeight: Int? = null

    @SerializedName("medium")
    @Expose
    var medium: String? = null

    @SerializedName("medium-width")
    @Expose
    var mediumWidth: Int? = null

    @SerializedName("medium-height")
    @Expose
    var mediumHeight: Int? = null

    @SerializedName("medium_large")
    @Expose
    var mediumLarge: String? = null

    @SerializedName("medium_large-width")
    @Expose
    var mediumLargeWidth: Int? = null

    @SerializedName("medium_large-height")
    @Expose
    var mediumLargeHeight: Int? = null

    @SerializedName("large")
    @Expose
    var large: String? = null

    @SerializedName("large-width")
    @Expose
    var largeWidth: Int? = null

    @SerializedName("large-height")
    @Expose
    var largeHeight: Int? = null

    @SerializedName("1536x1536")
    @Expose
    private var _1536x1536: String? = null

    @SerializedName("1536x1536-width")
    @Expose
    private var _1536x1536Width: Int? = null

    @SerializedName("1536x1536-height")
    @Expose
    private var _1536x1536Height: Int? = null

    @SerializedName("2048x2048")
    @Expose
    private var _2048x2048: String? = null

    @SerializedName("2048x2048-width")
    @Expose
    private var _2048x2048Width: Int? = null

    @SerializedName("2048x2048-height")
    @Expose
    private var _2048x2048Height: Int? = null
    fun get1536x1536(): String? {
        return _1536x1536
    }

    fun set1536x1536(_1536x1536: String?) {
        this._1536x1536 = _1536x1536
    }

    fun get1536x1536Width(): Int? {
        return _1536x1536Width
    }

    fun set1536x1536Width(_1536x1536Width: Int?) {
        this._1536x1536Width = _1536x1536Width
    }

    fun get1536x1536Height(): Int? {
        return _1536x1536Height
    }

    fun set1536x1536Height(_1536x1536Height: Int?) {
        this._1536x1536Height = _1536x1536Height
    }

    fun get2048x2048(): String? {
        return _2048x2048
    }

    fun set2048x2048(_2048x2048: String?) {
        this._2048x2048 = _2048x2048
    }

    fun get2048x2048Width(): Int? {
        return _2048x2048Width
    }

    fun set2048x2048Width(_2048x2048Width: Int?) {
        this._2048x2048Width = _2048x2048Width
    }

    fun get2048x2048Height(): Int? {
        return _2048x2048Height
    }

    fun set2048x2048Height(_2048x2048Height: Int?) {
        this._2048x2048Height = _2048x2048Height
    }
}