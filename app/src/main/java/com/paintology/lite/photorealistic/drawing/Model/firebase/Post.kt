package com.paintology.lite.photorealistic.drawing.Model.firebase

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("images") val images: Images? = null,
    @SerializedName("visibility") val visibility: String? = null,
    @SerializedName("level") val level: String? = null,
    @SerializedName("created_at") val createdAt: Any? = null,
    @SerializedName("categories") val categories: List<Category>? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("excerpt") val excerpt: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("ref") val ref: String? = null
)
