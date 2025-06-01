package com.paintology.lite.photorealistic.drawing.Activity.notifications.models

data class CommunityPostNotification(
    var postId: Any? = null,
    var id: String? = null,
    var drawingId: String? = null,
    var statistic: Statistic? = null,
    var images: Images? = null,
    var userId: String? = null,
    var author: Author? = null,
    var createdAt: Any? = null,
    var description: String? = null,
    var lastComments: List<Comment> = listOf(),
    var links: Links? = null,
    var title: String? = null,
    var legacyData: LegacyData? = null,
    var tags: List<String> = listOf(),
    var isDownloaded: Boolean = false,
    var isLiked: Boolean = false
) {
    constructor(map: Map<String, Any>) : this() {
        postId = map["post_id"]
        id = map["id"] as String?
        drawingId = map["drawing_id"] as String?
        statistic = if (map.containsKey("statistic")) Statistic(map["statistic"] as Map<String, Any>) else null
        images = if (map.containsKey("images")) Images(map["images"] as Map<String, Any>) else null
        userId = (map["author"] as Map<String, Any>)["user_id"] as String?
        author = if (map.containsKey("author")) Author(map["author"] as Map<String, Any>) else null
        createdAt = when (val createdAtValue = map["created_at"]) {
            is String -> createdAtValue
            is com.google.firebase.Timestamp -> formatTimestampToString(createdAtValue)
            else -> ""
        }
        description = map["description"] as String?
        links = if (map.containsKey("links")) Links(map["links"] as Map<String, Any>) else null
        title = map["title"] as String?
        lastComments = if (map.containsKey("last_comments")) {
            (map["last_comments"] as List<Map<String, Any>>).map { Comment(it) }
        } else listOf()
        legacyData = if (map.containsKey("legacy_data")) LegacyData(map["legacy_data"] as Map<String, Any>) else null
        tags = map["tags"] as List<String>
        isDownloaded = map["isDownloaded"] as Boolean? ?: false
        isLiked = map["isLiked"] as Boolean? ?: false
    }

    companion object {
        fun formatTimestampToString(timestamp: com.google.firebase.Timestamp): String {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(timestamp.toDate())
        }
    }

    data class Statistic(
        var comments: Int = 0,
        var views: Int = 0,
        var likes: Int = 0
    ) {
        constructor(map: Map<String, Any>) : this() {
            comments = (map["comments"] as? Long)?.toInt() ?: (map["comments"] as? Int ?: 0)
            views = (map["views"] as? Long)?.toInt() ?: (map["views"] as? Int ?: 0)
            likes = (map["likes"] as? Long)?.toInt() ?: (map["likes"] as? Int ?: 0)
        }
    }

    data class Images(
        var contentResized: String? = null,
        var content: String? = null
    ) {
        constructor(map: Map<String, Any>) : this() {
            contentResized = map["content_resized"] as String?
            content = map["content"] as String?
        }
    }

    data class Author(
        var userId: String? = null,
        var name: String? = null,
        var avatar: String? = null
    ) {
        constructor(map: Map<String, Any>) : this() {
            userId = map["user_id"] as String?
            name = map["name"] as String?
            avatar = map["avatar"] as String?
        }
    }

    data class Links(
        var redirect: String? = null,
        var youtube: String? = null
    ) {
        constructor(map: Map<String, Any>) : this() {
            redirect = map["redirect"] as String?
            youtube = map["youtube"] as String?
        }
    }

    data class LegacyData(
        var categoryId: Int? = null,
        var postType: Int = 0
    ) {
        constructor(map: Map<String, Any>) : this() {
            categoryId = (map["category_id"] as? Long)?.toInt() ?: (map["category_id"] as? Int)
            postType = (map["post_type"] as? Long)?.toInt() ?: (map["post_type"] as? Int ?: 0)
        }
    }

    data class Comment(
        var country: String? = null,
        var userId: String? = null,
        var name: String? = null,
        var createdAt: String? = null,
        var comment: String? = null,
        var avatar: String? = null
    ) {
        constructor(map: Map<String, Any>) : this() {
            country = map["country"] as String?
            userId = map["user_id"] as String?
            name = map["name"] as String?
            createdAt = when (val createdAtValue = map["created_at"]) {
                is String -> createdAtValue
                is com.google.firebase.Timestamp -> formatTimestampToString(createdAtValue)
                else -> ""
            }
            comment = map["comment"] as String?
            avatar = map["avatar"] as String?
        }
    }
}
