package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Blogpost {
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
    var postAuthor: String? = null

    @SerializedName("post_date")
    @Expose
    var postDate: String? = null

    @SerializedName("post_date_gmt")
    @Expose
    var postDateGmt: String? = null

    @SerializedName("post_content_html")
    @Expose
    var postContentHTML: String? = null

    @SerializedName("post_content_text")
    @Expose
    var postContentText: String? = null

    @SerializedName("post_excerpt")
    @Expose
    var postExcerpt: String? = null

    @SerializedName("post_status")
    @Expose
    var postStatus: String? = null

    @SerializedName("comment_status")
    @Expose
    var commentStatus: String? = null

    @SerializedName("ping_status")
    @Expose
    var pingStatus: String? = null

    @SerializedName("post_password")
    @Expose
    var postPassword: String? = null

    @SerializedName("post_name")
    @Expose
    var postName: String? = null

    @SerializedName("to_ping")
    @Expose
    var toPing: String? = null

    @SerializedName("pinged")
    @Expose
    var pinged: String? = null

    @SerializedName("post_modified")
    @Expose
    var postModified: String? = null

    @SerializedName("post_modified_gmt")
    @Expose
    var postModifiedGmt: String? = null

    @SerializedName("post_content_filtered")
    @Expose
    var postContentFiltered: String? = null

    @SerializedName("post_parent")
    @Expose
    var postParent: Int? = null

    @SerializedName("guid")
    @Expose
    var guid: String? = null

    @SerializedName("menu_order")
    @Expose
    var menuOrder: Int? = null

    @SerializedName("post_type")
    @Expose
    var postType: String? = null

    @SerializedName("post_mime_type")
    @Expose
    var postMimeType: String? = null

    @SerializedName("comment_count")
    @Expose
    var commentCount: String? = null

    @SerializedName("filter")
    @Expose
    var filter: String? = null

    @SerializedName("web_url")
    @Expose
    var webUrl: String? = null
}