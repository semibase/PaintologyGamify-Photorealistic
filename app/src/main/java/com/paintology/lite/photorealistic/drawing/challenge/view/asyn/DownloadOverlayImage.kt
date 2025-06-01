package com.paintology.lite.photorealistic.drawing.challenge.view.asyn

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL
import java.net.URLConnection

class DownloadOverlayImage(
    private val context: Context,
    private val _object: PostDetailModel
) {
    var fileName = ""
    var traceImageLink = ""

    init {
        if (_object.getVideo_and_file_list().get(0).getObj_overlaid() != null) {
            fileName = if (_object.getVideo_and_file_list().get(0).getObj_overlaid()
                    .getFilename() != null
            ) _object.getVideo_and_file_list().get(0).getObj_overlaid()
                .getFilename() else "overLaid.jpg"
            traceImageLink = _object.getVideo_and_file_list().get(0).getObj_overlaid().getUrl()
        } else {
            fileName = if (_object.getVideo_and_file_list().get(1).getObj_overlaid()
                    .getFilename() != null
            ) _object.getVideo_and_file_list().get(1).getObj_overlaid()
                .getFilename() else "overLaid.jpg"
            traceImageLink = _object.getVideo_and_file_list().get(1).getObj_overlaid().getUrl()
        }
    }

    suspend fun executeAsyncTask(): ArrayList<String>? {
        return withContext(Dispatchers.IO) {
            val filesList = downloadTextFiles()
            val imagePath = downloadImage()
            filesList.add(imagePath ?: "")
            filesList
        }
    }

    private fun downloadTextFiles(): ArrayList<String> {
        val lstFileNames = ArrayList<String>()
        val file1 = File(KGlobal.getStrokeEventFolderPath(context))
        if (!file1.exists()) {
            file1.mkdirs()
        }
        for (i in 0 until 2) {
            val textFileLink = _object.getVideo_and_file_list().get(i).getObj_text_files().getUrl()
            val fileName = _object.getVideo_and_file_list().get(i).getObj_text_files().getFilename()

            val file = File(file1, fileName)
            if (file.exists()) {
                lstFileNames.add(file.absolutePath)
            } else {
                try {
                    val url = URL(textFileLink)
                    val ucon: URLConnection = url.openConnection()
                    ucon.setReadTimeout(50000)
                    ucon.setConnectTimeout(100000)

                    val `is`: InputStream = ucon.getInputStream()
                    val inStream = BufferedInputStream(`is`, 1024 * 5)

                    if (file.exists()) {
                        lstFileNames.add(file.absolutePath)
                        break
                    }
                    val outStream = FileOutputStream(file)
                    val buff = ByteArray(5 * 1024)

                    var len: Int
                    while (inStream.read(buff).also { len = it } != -1) {
                        outStream.write(buff, 0, len)
                    }
                    outStream.flush()
                    outStream.close()
                    inStream.close()
                    lstFileNames.add(file.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return lstFileNames
    }

    private fun downloadImage(): String? {
        var imagePath: String? = null
        try {
            val file = File(KGlobal.getTraceImageFolderPath(context), fileName)
            if (file.exists()) {
                imagePath = file.absolutePath
            } else {
                val url = URL(traceImageLink)
                val inputStream: InputStream = url.openConnection().getInputStream()
                val bm: Bitmap = BitmapFactory.decodeStream(inputStream)

                val path = File(KGlobal.getTraceImageFolderPath(context))
                if (!path.exists()) {
                    path.mkdirs()
                }
                val imageFile = File(path, fileName)
                val out: FileOutputStream = FileOutputStream(imageFile)
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

    fun postExecute(lst_main: ArrayList<String>) {
        StringConstants.IsFromDetailPage = false
        val intent = Intent(
            context,
            PaintActivity::class.java
        )
        if (!_object.getCanvas_color().isEmpty()) {
            intent.putExtra("canvas_color", _object.getCanvas_color())
        }
        val youtubeLink = _object.getYoutube_link_list()
        if (youtubeLink != null) {
            val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
            intent.putExtra("youtube_video_id", _youtube_id)
        }
        intent.action = "YOUTUBE_TUTORIAL_WITH_OVERLAID"
        if (lst_main.size == 2) {
            intent.putExtra("StrokeFilePath", lst_main.get(0))
            intent.putExtra("EventFilePath", lst_main.get(1))
        }
        intent.putExtra(
            "OverlaidImagePath",
            File(
                KGlobal.getTraceImageFolderPath(context),
                fileName
            ).absolutePath
        )
        intent.putExtra("id", _object.id)
        intent.putExtra("drawingType", "TUTORAILS")
        context.startActivity(intent)
    }
}
