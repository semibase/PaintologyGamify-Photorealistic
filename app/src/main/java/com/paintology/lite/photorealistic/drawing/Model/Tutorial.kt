package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Tutorial {
    @SerializedName("ID")
    @Expose
    var id: Int? = null

    @SerializedName("post_author")
    @Expose
    var postAuthor: String? = null

    @SerializedName("post_date")
    @Expose
    var postDate: String? = null

    @SerializedName("post_date_gmt")
    @Expose
    var postDateGmt: String? = null

    @SerializedName("post_title")
    @Expose
    var postTitle: String? = null

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

    @SerializedName("Rating")
    @Expose
    var rating: Int? = null

    @SerializedName("thumb_url")
    @Expose
    var thumbUrl: String? = null

    @SerializedName("categoryURL")
    @Expose
    var categoryURL: String? = null

    @SerializedName("VisitPage")
    @Expose
    var visitPage: String? = null

    @SerializedName("ResizeImage")
    @Expose
    var resizeImage: String? = null

    @SerializedName("post_content_html")
    @Expose
    var postContentHTML: String? = null

    @SerializedName("post_content")
    @Expose
    var postContentText: String? = null

    @SerializedName("EmbededImage")
    @Expose
    var embededImage: List<Any>? = null

    @SerializedName("videos_and_files")
    @Expose
    var videosAndFiles: List<VideosAndFile>? = null

    @SerializedName("redirect_url")
    @Expose
    var redirectUrl: String? = null

    @SerializedName("youtube_link")
    @Expose
    var youtubeLink: String? = null

    @SerializedName("canvas_color")
    @Expose
    var canvasColor: String? = null

    @SerializedName("color_swatch")
    @Expose
    var colorSwatch: List<ColorSwatch>? = null

    @SerializedName("membership_plan")
    @Expose
    var membershipPlan: String? = null

    @SerializedName("RelatedPostsData")
    @Expose
    var relatedPostsData: List<RelatedPostsDatum>? = null
}