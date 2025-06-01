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

class DownloadOverlayFromDoDrawing(
    private val context: Context,
    private val isFromTrace: Boolean,
    private val _object: PostDetailModel,
) {
    var fileName = ""
    var traceImageLink = ""

    init {
        val data = _object.getVideo_and_file_list()[0]
        fileName = if (isFromTrace) data.getObj_trace_image().getFilename() else data.getObj_overlaid().getFilename()
        traceImageLink = if (isFromTrace) data.getObj_trace_image().getUrl() else data.getObj_overlaid().getUrl()
    }

    suspend fun executeAsyncTask(): String? {
        return withContext(Dispatchers.IO) {
            val path = downloadOverlay()
            path
        }
    }

    private fun downloadOverlay(): String? {
        var imagePath: String? = null
        try {
            val file = File(KGlobal.getTraceImageFolderPath(context), fileName)
            if (file.exists()) {
                imagePath = file.absolutePath
            } else {
                val url = URL(traceImageLink)
                val inputStream = url.openConnection().getInputStream()
                val bm = BitmapFactory.decodeStream(inputStream)

                val path = File(KGlobal.getTraceImageFolderPath(context))
                if (!path.exists()) {
                    path.mkdirs()
                }
                val imageFile = File(path, fileName)
                val out = FileOutputStream(imageFile)
                bm.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                imagePath = imageFile.absolutePath
            }
        } catch (e: IOException) {
            Log.e("TAGG", "Exception at download " + e.message)
        }
        return imagePath
    }

    suspend fun postExecute(path: String?) {
        try {

            StringConstants.IsFromDetailPage = false
            if (isFromTrace) {
                val intent = Intent(
                    context,
                    PaintActivity::class.java
                )
                intent.action = "Edit Paint"
                intent.putExtra("FromLocal", true)
                intent.putExtra("drawingType", "TUTORAILS")
                intent.putExtra("paint_name", path)
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color())
                }
                val swatches = _object.swatches
                val gson = Gson()
                val swatchesJson = gson.toJson(swatches)
                intent.putExtra("swatches", swatchesJson)
                intent.putExtra("id", _object.id)
                context.startActivity(intent)
            } else {
                val intent = Intent(
                    context,
                    PaintActivity::class.java
                )
                intent.action = "LoadWithoutTrace"
                intent.putExtra("path", fileName)
                intent.putExtra("drawingType", "TUTORAILS")
                intent.putExtra(
                    "ParentFolderPath",
                    KGlobal.getTraceImageFolderPath(context)
                )
                if (!_object.getCanvas_color().isEmpty()) {
                    intent.putExtra("canvas_color", _object.getCanvas_color())
                }
                val swatches = _object.swatches
                val gson = Gson()
                val swatchesJson = gson.toJson(swatches)
                intent.putExtra("swatches", swatchesJson)
                intent.putExtra("id", _object.id)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("TAGGG", "Exception at post " + e.toString())
        }
    }

    private fun openTutorialsRewardPoint() {
        // Implement your logic for opening tutorials or rewarding points
    }
}
