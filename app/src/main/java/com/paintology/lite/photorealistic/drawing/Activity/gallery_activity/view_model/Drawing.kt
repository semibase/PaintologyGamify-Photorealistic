package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.view_model

data class DrawingItem(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val type: String,
    val referenceId: String,
    val tags: String,
    val imagesContent: String,
    val metadataPath: String,
    val metadataParentFolderPath: String,
    val metadataTutorialId: String,
    val statisticsComments: Int,
    val statisticsLikes: Int,
    val statisticsRatings: Int,
    val statisticsReviewsCount: Int,
    val statisticsShares: Int,
    val statisticsViews: Int,
    val author: Author,
    val linksYoutube: String
)

data class Author(
    val userId: String,
    val name: String,
    val avatar: String,
    val country: String,
    val level: String
)

data class Page(
    val pageNo: Int,
    val perPage: Int
)

data class FacetCount(
    val count: Int
)

data class Drawing(
    val data: List<DrawingItem>,
    val page: Page,
    val facetCounts: List<FacetCount>
)
