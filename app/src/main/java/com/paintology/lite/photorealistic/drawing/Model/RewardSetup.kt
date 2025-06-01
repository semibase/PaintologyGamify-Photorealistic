package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RewardSetup(

    @SerializedName("default_bonus_point")
    @Expose
    val default_bonus_point: Int? = 0,

    @SerializedName("opening_tutorials")
    @Expose
    val opening_tutorials: Int? = null,

    @SerializedName("draw_stroke")
    @Expose
    val draw_stroke: Int? = null,

    @SerializedName("community_post_posted")
    @Expose
    val community_post_posted: Int? = null,

    @SerializedName("community_post_like")
    @Expose
    val community_post_like: Int? = null,

    @SerializedName("community_post_comment")
    @Expose
    val community_post_comment: Int? = null,

    @SerializedName("community_post_share")
    @Expose
    val community_post_share: Int? = null,

    @SerializedName("community_post_posted_hashtag")
    @Expose
    val community_post_posted_hashtag: Int? = null,

    @SerializedName("draw_overlay_image")
    @Expose
    val draw_overlay_image: Int? = null,

    @SerializedName("draw_trace_image")
    @Expose
    val draw_trace_image: Int? = null,

    @SerializedName("draw_camera_image")
    @Expose
    val draw_camera_image: Int? = null,

    @SerializedName("google_classroom")
    @Expose
    val google_classroom: Int? = null,

    @SerializedName("quora_paintology")
    @Expose
    val quora_paintology: Int? = null,

    @SerializedName("my_paintings_add_text")
    @Expose
    val my_paintings_add_text: Int? = null,

    @SerializedName("my_paintings_share")
    @Expose
    val my_paintings_share: Int? = null,

    @SerializedName("paintology_website")
    @Expose
    val paintology_website: Int? = null,

    @SerializedName("paintology_youtube")
    @Expose
    val paintology_youtube: Int? = null,

    @SerializedName("learn_drawing")
    @Expose
    val learn_drawing: Int? = null,

    @SerializedName("apps_by_paintology")
    @Expose
    val apps_by_paintology: Int? = null,

    @SerializedName("online_tutorial")
    @Expose
    val online_tutorial: Int? = null,

    @SerializedName("blog_website")
    @Expose
    val blog_website: Int? = null,

    @SerializedName("my_movies")
    @Expose
    val my_movies: Int? = null,

    @SerializedName("movies_share")
    @Expose
    val movies_share: Int? = null,

    )
