package com.paintology.lite.photorealistic.drawing.challenge.view.asyn

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class DownloadsImage(
    private val context: Context,
    private val youtubeLink: String,
    private val _object: PostDetailModel,
    private val isFromTrace: Boolean = false
) {
    private var traceImageLink: String = ""
    private var fileName: String = ""

    init {
        traceImageLink = _object.getVideo_and_file_list().get(0).obj_overlaid.getUrl()
        fileName = _object.getVideo_and_file_list().get(0).obj_overlaid.getFilename() ?: ""
    }

    suspend fun executeAsyncTask(): String? {
        return withContext(Dispatchers.IO) {
            val path = downloadImage()
            path
        }
    }

    private fun downloadImage(): String? {
        var imageFileAbsolutePath: String? = null
        try {
            val url = URL(traceImageLink)
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val bm = BitmapFactory.decodeStream(inputStream)

            val path = File(KGlobal.getTraceImageFolderPath(context))
            if (!path.exists()) {
                path.mkdirs()
            }

            val imageFile =
                File(path, traceImageLink.substring(traceImageLink.lastIndexOf('/') + 1))
            val out = FileOutputStream(imageFile)
            bm.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            imageFileAbsolutePath = imageFile.absolutePath
        } catch (e: IOException) {
            Log.e("TAGG", "Exception at download " + e.message)
        }
        return imageFileAbsolutePath
    }

    suspend fun postExecute(path: String?) {
        try {

            if (isFromTrace) {
                StringConstants.IsFromDetailPage = false
                val intent = Intent(context, PaintActivity::class.java).apply {
                    putExtra("youtube_video_id", youtubeLink)
                    action = "YOUTUBE_TUTORIAL"
                    putExtra("drawingType", "TUTORAILS")
                    putExtra("paint_name", path)
                    if (!_object.getCanvas_color().isEmpty()) {
                        putExtra("canvas_color", _object.getCanvas_color())
                    }
                    putExtra("id", _object.id)
                }
                context.startActivity(intent)
            } else {
                StringConstants.IsFromDetailPage = false
                val intent = Intent(context, PaintActivity::class.java).apply {
                    action = "LoadWithoutTrace"
                    putExtra("drawingType", "TUTORAILS")
                    putExtra("path", fileName)
                    putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(context))
                    putExtra("youtube_video_id", youtubeLink)
                    if (_object.getCanvas_color().isNotEmpty()) {
                        putExtra("canvas_color", _object.getCanvas_color())
                    }

                    val swatchesJson = Gson().toJson(_object.swatches)
                    putExtra("swatches", swatchesJson)
                    putExtra("id", _object.id)
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("TAGGG", "Exception at post " + e.toString())
        }
    }

}
