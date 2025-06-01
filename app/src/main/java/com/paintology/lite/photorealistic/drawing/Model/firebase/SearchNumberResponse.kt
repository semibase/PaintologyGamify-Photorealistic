package com.paintology.lite.photorealistic.drawing.Model.firebase

import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch

import com.google.gson.annotations.SerializedName


data class SearchNumberResponse(
    @SerializedName("searchResults")
    val searchResults: FirebaseTutorial,
)

data class SearchResults(
    @SerializedName("images")
    val images: Images,
    @SerializedName("visibility")
    val visibility: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("color_swatch")
    val colorSwatch: List<ColorSwatch>? = null,
)
