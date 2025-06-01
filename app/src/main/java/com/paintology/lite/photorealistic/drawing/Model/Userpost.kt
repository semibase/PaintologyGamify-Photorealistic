package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Userpost {

    @SerializedName("ID")
    @Expose
    var id: Int? = null

    @SerializedName("post_title")
    @Expose
    var postTitle: String? = null

    @SerializedName("thumb_url")
    @Expose
    var thumbUrl: String? = null

    @SerializedName("post_author")
    @Expose
    val postAuthor: String? = null

    @SerializedName("post_date")
    @Expose
    val postDate: String? = null

    @SerializedName("post_date_gmt")
    @Expose
    val postDateGmt: String? = null

    @SerializedName("post_content_html")
    @Expose
    var postContentHTML: String? = null

    @SerializedName("post_content_text")
    @Expose
    var postContentText: String? = null

    @SerializedName("post_excerpt")
    @Expose
    val postExcerpt: String? = null

    @SerializedName("post_status")
    @Expose
    val postStatus: String? = null

    @SerializedName("comment_status")
    @Expose
    val commentStatus: String? = null

    @SerializedName("ping_status")
    @Expose
    val pingStatus: String? = null

    @SerializedName("post_password")
    @Expose
    val postPassword: String? = null

    @SerializedName("post_name")
    @Expose
    val postName: String? = null

    @SerializedName("to_ping")
    @Expose
    val toPing: String? = null

    @SerializedName("pinged")
    @Expose
    val pinged: String? = null

    @SerializedName("post_modified")
    @Expose
    val postModified: String? = null

    @SerializedName("post_modified_gmt")
    @Expose
    val postModifiedGmt: String? = null

    @SerializedName("post_content_filtered")
    @Expose
    val postContentFiltered: String? = null

    @SerializedName("post_parent")
    @Expose
    val postParent: Int? = null

    @SerializedName("guid")
    @Expose
    val guid: String? = null

    @SerializedName("menu_order")
    @Expose
    val menuOrder: Int? = null

    @SerializedName("post_type")
    @Expose
    val postType: String? = null

    @SerializedName("post_mime_type")
    @Expose
    val postMimeType: String? = null

    @SerializedName("comment_count")
    @Expose
    val commentCount: String? = null

    @SerializedName("filter")
    @Expose
    val filter: String? = null
}