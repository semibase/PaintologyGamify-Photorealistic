package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.profile_model

data class UserProfile(
    var country: String,
    var username: String = "",
    var statistic: Statistic,
    var preferences: Preferences,
    var social: Social,
    var level: String,
    var name: String,
    var bio: String,
    var features: List<String>,
    var brushes: List<String>,
    val createdAt: String,
    var avatar: String,
    val points: Int,
    val progress: Progress,
    val auth_provider: String,
    var age: String,
    val email: String,
    var gender: String
)

data class UserUpdateProfile(
    var country: String,
    var preferences: Preferences,
    var social: Social,
    var bio: String,
    var avatar: String,
    var name: String,
    var age: String,
    var gender: String
)

data class UserUpdateProfileFlag(
    var finish_intro: Boolean
)

data class Statistic(
    var totalPosts: Int,
    var totalFollowers: Int,
    var totalFollowing: Int
)

data class Progress(
    var gallery: Int,
    var tutorial: Int,
    var community: Int,
    var drawing: Int,
    var big_points: Int,
    var painting: Int,
    var resource: Int
)

data class Preferences(
    var art: Art
)

data class Art(
    var favorites: List<HashMap<*, *>>,
    var ability: String,
    var mediums: List<HashMap<*, *>>
)

data class Social(
    var youtube: String,
    var other: String,
    var twitter: String,
    var website: String,
    var quora: String,
    var tiktok: String,
    var facebook: String,
    var pinterest: String,
    var instagram: String,
    var linkedin: String,
    var paintology: String? = ""
)
