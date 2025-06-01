package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.paintology.lite.photorealistic.drawing.Model.CategoryModel.categoryData

class SearchResponse {

    @SerializedName("status")
    @Expose
    val status: String? = null

    @SerializedName("response")
    @Expose
    val response: String? = null

    @SerializedName("code")
    @Expose
    val code: Int? = null

    @SerializedName("data")
    @Expose
    val data: Data? = null

    class Data {
        @SerializedName("isNumberSearch")
        @Expose
        var isNumberSearch: Boolean = false

        @SerializedName("searched_number_is")
        @Expose
        var searchedNumberIs: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("count")
        @Expose
        var count: Int = 0

        @SerializedName("parent_name")
        @Expose
        var parentName: String? = null

        @SerializedName("parent_category_id")
        @Expose
        var parentCategoryId: Int = 0

        @SerializedName("tutorials")
        @Expose
        var tutorials: List<Tutorial>? = null

        @SerializedName("blogpost")
        @Expose
        var blogpost: List<Blogpost>? = null

        @SerializedName("userpost")
        @Expose
        var userpost: List<Userpost>? = null

        @SerializedName("Childs")
        @Expose
        var childs: ArrayList<categoryData>? = null

        @SerializedName("search_reponse")
        @Expose
        var searchResponse: String? = null
    }
}