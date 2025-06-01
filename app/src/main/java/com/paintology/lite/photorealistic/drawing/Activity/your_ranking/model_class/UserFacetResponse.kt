package com.paintology.lite.photorealistic.drawing.Activity.your_ranking.model_class

import com.google.gson.annotations.SerializedName

data class UserFacetResponse(
    @SerializedName("facet_counts")
    val facetCounts: List<UserFacetCount>
)

data class UserFacetCount(
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
