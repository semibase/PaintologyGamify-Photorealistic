package com.paintology.lite.photorealistic.drawing.challenge.view

import android.util.Log
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch
import com.paintology.lite.photorealistic.drawing.Model.ContentSectionModel
import com.paintology.lite.photorealistic.drawing.Model.Overlaid
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.Model.RelatedPostsData
import com.paintology.lite.photorealistic.drawing.Model.sizes
import com.paintology.lite.photorealistic.drawing.Model.text_files
import com.paintology.lite.photorealistic.drawing.Model.trace_image
import com.paintology.lite.photorealistic.drawing.Model.videos_and_files
import org.json.JSONObject

fun PostDetailModel.calculateTutorialType(): Tutorial_Type {
    return when {
       getVideo_and_file_list() != null && (getVideo_and_file_list()?.size ?: 0 ) >= 2 &&
                (getVideo_and_file_list()[0].getObj_text_files() != null &&
                       getVideo_and_file_list()[1].getObj_text_files() != null) &&
                (getYoutube_link_list() != null && !getYoutube_link_list().isEmpty()) -> {
            if (getVideo_and_file_list().get(0).getObj_overlaid() != null ||
                   getVideo_and_file_list().get(1).getObj_overlaid() != null) {
                Tutorial_Type.Strokes_Overlaid_Window
            } else if (getVideo_and_file_list().get(0).getObj_trace_image() == null ||
                   getVideo_and_file_list().get(1).getObj_trace_image() == null) {
                Tutorial_Type.Strokes_Window
            } else {
                Tutorial_Type.Strokes_Window
            }
        }
       getVideo_and_file_list() != null &&(getVideo_and_file_list()?.size ?: 0 ) > 0 &&
               getVideo_and_file_list().get(0).getObj_trace_image() != null &&
                (getYoutube_link_list() != null && getYoutube_link_list().isNotEmpty()) -> {
            Tutorial_Type.Video_Tutorial_Trace
        }
       getVideo_and_file_list() != null &&(getVideo_and_file_list()?.size ?: 0 ) > 0 &&
               getVideo_and_file_list().get(0).getObj_overlaid() != null &&
                (getYoutube_link_list() != null && getYoutube_link_list().isNotEmpty()) -> {
            Tutorial_Type.Video_Tutorial_Overraid
        }
      getVideo_and_file_list() != null &&(getVideo_and_file_list()?.size ?: 0 ) > 0 &&
               getVideo_and_file_list().get(0).getObj_overlaid() != null &&
               getYoutube_link_list().isEmpty() -> {
            Tutorial_Type.DO_DRAWING_OVERLAY
        }
       getVideo_and_file_list() != null && (getVideo_and_file_list()?.size ?: 0 ) > 0 &&
               getVideo_and_file_list()[0].getObj_trace_image() != null &&
               getYoutube_link_list().isEmpty() -> {
            Tutorial_Type.DO_DRAWING_TRACE
        }
       getExternal_link() != null && getExternal_link().isNotEmpty() -> {
            if (getExternal_link().contains("youtu.be")) {
                Tutorial_Type.SeeVideo_From_External_Link
            } else {
                Tutorial_Type.Read_Post
            }
        }
       getYoutube_link_list() != null && getYoutube_link_list().isNotEmpty() -> {
            Tutorial_Type.See_Video
        }
        else -> {
            Tutorial_Type.READ_POST_DEFAULT
        }
    }
}

fun JSONObject.toPostDetailModel(): PostDetailModel {
    val _lst_video_file = ArrayList<videos_and_files>()
    val objectFirst = this

    val postDetailModel = PostDetailModel().apply {
        ID = objectFirst.optString("ID", "")
        categoryName = objectFirst.optString("categoryName", "")
        categoryURL = objectFirst.optString("categoryURL", "")
        external_link = objectFirst.optString("external_link", "")
        canvas_color = objectFirst.optString("canvas_color", "")
        visitPage = objectFirst.optString("VisitPage", "")
        membership_plan = objectFirst.optString("membership_plan", "")
        post_content = objectFirst.optString("post_content", "")
        post_date = objectFirst.optString("post_date", "")
        post_title = objectFirst.optString("post_title", "")
        rating = objectFirst.optString("Rating", "")
        text_descriptions = objectFirst.optString("text_descriptions", "")
        thumb_url = objectFirst.optString("thumb_url", "")
        youtube_link_list = objectFirst.optString("youtube_link", "")

        if (objectFirst.has("color_swatch") && !objectFirst.isNull("color_swatch")) {
            val swatchesArray = objectFirst.getJSONArray("color_swatch")
            val swatcharray = ArrayList<ColorSwatch>()

            if (swatchesArray != null && swatchesArray.length() > 0) {
                for (i in 0 until swatchesArray.length()) {
                    val swatch = swatchesArray.getJSONObject(i).getString("color_swatch")
                    val colorSwatch = ColorSwatch().apply { color_swatch = swatch }
                    swatcharray.add(colorSwatch)
                }
            }
            this.setSwatches(swatcharray)
        }

        if (objectFirst.has("ResizeImage") && !objectFirst.isNull("ResizeImage")) {
            resizeImage = objectFirst.optString("ResizeImage")
        }
    }

    if (objectFirst.has("RelatedPostsData")) {
        val related_list_json = objectFirst.getJSONArray("RelatedPostsData")
        val related_List = ArrayList<RelatedPostsData>()
        if (related_list_json != null && related_list_json.length() > 0) {
            for (i in 0 until related_list_json.length()) {
                val obj_related = RelatedPostsData()
                val obj = related_list_json.getJSONObject(i)
                obj_related.id = obj.optInt("ID", 0)
                obj_related.post_title = obj.optString("post_title", "")
                obj_related.thumbImage = obj.optString("thumbImage", "")
                related_List.add(obj_related)
            }
            postDetailModel.list_related_post = related_List
        }
    }

    val contentSectionList = ArrayList<ContentSectionModel>()
    val obj_content = ContentSectionModel().apply {
        url = postDetailModel.thumb_url
        caption = "Featured"
        videoContent = false
    }
    contentSectionList.add(obj_content)

    if (objectFirst.has("EmbededData")) {
        val embededVideoList = objectFirst.getJSONArray("EmbededData")
        for (i in 0 until embededVideoList.length()) {
            val obj_content = ContentSectionModel()
            val obj = embededVideoList.getJSONObject(i)
            obj_content.url = obj.optString("EmbededPath", "")
            obj_content.caption = obj.optString("Caption", "")

            if (obj_content.url != null && obj_content.url.isNotEmpty() && obj_content.url.contains("youtu.be")) {
                if (obj_content.url.contains("youtu.be")) {
                    obj_content.videoContent = true
                    val _youtube_id = obj_content.url.replace("https://youtu.be/", "").replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    obj_content.youtube_url = "http://img.youtube.com/vi/$_youtube_id/0.jpg"
                }
            }
            contentSectionList.add(obj_content)
        }
    }

    try {
        if (objectFirst.has("EmbededImage")) {
            val embededImageList = objectFirst.getJSONArray("EmbededImage")
            for (i in 0 until embededImageList.length()) {
                val obj = embededImageList.getJSONObject(i)
                val obj_content = ContentSectionModel()
                obj_content.url = obj.optString("EmbededPath", "")
                obj_content.caption = obj.optString("Caption", "")
                obj_content.videoContent = false
                contentSectionList.add(obj_content)
            }
        }
    } catch (e: Exception) {
        Log.e("TAGG", "Exception at parseembeddd image " + e.message)
    }
    postDetailModel.featuredImage = contentSectionList

    if (objectFirst.has("videos_and_files")) {
        val videoArray = objectFirst.optJSONArray("videos_and_files")
        if (videoArray != null) {
            for (i in 0 until videoArray.length()) {
                val obj = videoArray.optJSONObject(i)
                val videos_and_files = videos_and_files().apply {
                    obj.optJSONObject("text_file")?.let { obj_text ->
                        val obj_text_file = text_files().apply {
                            ID = obj_text.optInt("ID", 0)
                            title = obj_text.optString("title", "")
                            icon = obj_text.optString("icon", "")
                            filename = obj_text.optString("filename", "")
                            url = obj_text.optString("url", "")
                        }
                        obj_text_files = obj_text_file
                    }

                    if (obj.optJSONObject("trace_image") != null) {
                        val obj_trace_object = obj.getJSONObject("trace_image")
                        trace_image().apply {
                            ID = obj_trace_object.optInt("ID", 0)
                            title = obj_trace_object.optString("title", "")
                            icon = obj_trace_object.optString("icon", "")
                            filename = obj_trace_object.optString("filename", "")
                            url = obj_trace_object.optString("url", "")
                            if (obj_trace_object.has("sizes")) {
                                val objSize = obj_trace_object.getJSONObject("sizes")
                                sizes().apply {
                                    large = objSize.optString("large", "")
                                }.also { obj_sizes = it }
                            } else {
                                obj_sizes = null
                            }
                        }.also { obj_trace_image = it }
                    } else {
                        obj_trace_image = null
                    }

                    if (obj.optJSONObject("overlay_image") != null) {
                        val obj_overlaid_object = obj.getJSONObject("overlay_image")
                        Overlaid().apply {
                            title = obj_overlaid_object.optString("title", "")
                            filename = obj_overlaid_object.optString("filename", "")
                            url = obj_overlaid_object.optString("url", "")
                        }.also { obj_overlaid = it }
                    } else {
                        obj_overlaid = null
                    }
                }
                _lst_video_file.add(videos_and_files)
            }
        }
        if (_lst_video_file.isNotEmpty()) {
            postDetailModel.video_and_file_list = _lst_video_file
        }
    }

    return postDetailModel
}

