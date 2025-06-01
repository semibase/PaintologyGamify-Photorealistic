package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model

import com.google.gson.annotations.SerializedName

data class Drawing(
    @SerializedName("data")
    val data: List<DrawingItem>,
    @SerializedName("page")
    val page: Page,
    @SerializedName("facet_counts")
    val facetCounts: List<FacetCount>
)

data class DrawingItem(
    @SerializedName("images")
    val images: Images,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("statistic")
    val statistic: Statistic,
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("author")
    val author: Author,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("links")
    val links: Links,
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("tags")
    val tags: List<String>
)

data class Images(
    @SerializedName("content")
    val content: String
)

data class Metadata(
    @SerializedName("path")
    val path: String,
    @SerializedName("parent_folder_path")
    val parentFolderPath: String
)

data class Statistic(
    @SerializedName("shares")
    val shares: Int,
    @SerializedName("comments")
    val comments: Int,
    @SerializedName("ratings")
    val ratings: Int,
    @SerializedName("views")
    val views: Int,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("reviews_count")
    val reviewsCount: Int
)

data class Author(
    @SerializedName("country")
    val country: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("avatar")
    val avatar: String
)

data class Links(
    @SerializedName("youtube")
    val youtube: String
)

data class Page(
    @SerializedName("current")
    val current: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("total_elements")
    val totalElements: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)

data class FacetCount(
    @SerializedName("stats")
    val stats: Stats,
    @SerializedName("counts")
    val counts: List<Count>,
    @SerializedName("sampled")
    val sampled: Boolean,
    @SerializedName("field_name")
    val fieldName: String
)

data class Stats(
    @SerializedName("total_values")
    val totalValues: Int
)

data class Count(
    @SerializedName("highlighted")
    val highlighted: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("value")
    val value: String
)
