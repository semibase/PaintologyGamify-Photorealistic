package com.paintology.lite.photorealistic.drawing.Model.firebase

import com.paintology.lite.photorealistic.drawing.Model.CommunityPost


class SearchContentResponse (
    var community_posts: List<CommunityPost> = listOf(),
    var tutorials: List<Post> = listOf(),
    var posts: List<Post> = listOf()
)
