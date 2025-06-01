package com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper

import android.content.Context
import android.widget.Toast
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.DrawingViewActivity
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.views.activities.GalleryActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.startDrawingActivity
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.util.FireUtils

class DrawingUtils(var context: Context) {

    fun fetchGalleryPost(id: String) {

        val filterBy = "id:=$id"
        val filters = filterBy?.let {
            hashMapOf("filter_by" to it)
        }

        val sortBy = "created_at:desc"
        val sorts = sortBy?.let {
            hashMapOf("sort_by" to it)
        }

        FirebaseFirestoreApi.fetchDrawingList(1, 1, filters, sorts)
            .addOnCompleteListener {
                FireUtils.hideProgressDialog()
                if (it.isSuccessful) {
                    val data = it.result.data as HashMap<*, *>;
                    val dList = data["data"] as List<*>
                    if (dList.isNotEmpty()) {
                        val item = dList[0] as HashMap<*, *>
                        val drawing = parseDrawing(item)
                        context.startDrawingActivity(
                            drawing,
                            DrawingViewActivity::class.java,
                            false
                        )
                        return@addOnCompleteListener
                    }
                }
                Toast.makeText(context, "No Gallery Post Found", Toast.LENGTH_SHORT).show()
                context.openActivity(GalleryActivity::class.java)
            }
    }

    fun parseDrawing(data: HashMap<*, *>): NewDrawing {
        val id = data["id"] as? String ?: ""
        val title = data["title"] as? String ?: ""
        val description = data["description"] as? String ?: ""
        val createdAt = data["created_at"] as? String ?: ""
        val type = data["type"] as? String ?: ""
        val referenceId = data["reference_id"] as? String ?: ""
        val tags = data["tags"] as? List<String> ?: emptyList()

        val imagesData = data["images"] as? Map<*, *>
        val images = Images(content = imagesData?.get("content") as? String ?: "")

        val metadataData = data["metadata"] as? Map<*, *>
        val metadata = Metadata(
            path = metadataData?.get("path") as? String ?: "",
            parentFolderPath = metadataData?.get("parent_folder_path") as? String ?: "",
            tutorialId = metadataData?.get("tutorial_id") as? String ?: ""
        )


        val statisticsData = data["statistic"] as? Map<String, Any>
        val statistics =Statistic(
            comments = statisticsData?.get("comments") as? Int,
            likes = statisticsData?.get("likes") as? Int ?: 0,
            ratings = statisticsData?.get("ratings") as? Int ?: 0,
            reviewsCount = statisticsData?.get("reviews_count") as? Int ?: 0,
            shares = statisticsData?.get("shares") as? Int ?: 0,
            views = statisticsData?.get("views") as? Int ?: 0
        )

        val authorData = data["author"] as? Map<String, Any>
        val author = Author(
            userId = authorData?.get("user_id") as? String ?: "",
            name = authorData?.get("name") as? String ?: "",
            avatar = authorData?.get("avatar") as? String ?: "",
            country = authorData?.get("country") as? String,
            level = authorData?.get("level") as? String
        )
        val linksData = data["links"] as? Map<String, Any>
        val links = Links(youtube = linksData?.get("youtube") as? String ?: "")

        return NewDrawing(
            id = id,
            title = title,
            description = description,
            createdAt = createdAt,
            type = type,
            tags = tags,
            images = images,
            links = links,
            metadata = metadata,
            statistic = statistics,
            author = author,
            referenceId = referenceId
        )
    }
}