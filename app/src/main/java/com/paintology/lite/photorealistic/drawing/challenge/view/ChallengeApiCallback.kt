package com.paintology.lite.photorealistic.drawing.challenge.view

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.paintology.lite.photorealistic.drawing.Enums.Tutorial_Type
import com.paintology.lite.photorealistic.drawing.Model.ColorSwatch
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.api.ApiCallback
import com.paintology.lite.photorealistic.drawing.challenge.view.asyn.DownloadOverlayFromDoDrawing
import com.paintology.lite.photorealistic.drawing.challenge.view.asyn.DownloadOverlayImage
import com.paintology.lite.photorealistic.drawing.challenge.view.asyn.DownloadsImage
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity
import com.paintology.lite.photorealistic.drawing.minipaint.Play_YotubeVideo
import com.paintology.lite.photorealistic.drawing.room.daos.ColorSwatchDao
import com.paintology.lite.photorealistic.drawing.room.entities.ColorSwatchEntity
import com.paintology.lite.photorealistic.drawing.util.KGlobal
import com.paintology.lite.photorealistic.drawing.util.MyApplication
import com.paintology.lite.photorealistic.drawing.util.StringConstants
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.Executors

class ChallengeApiCallback constructor(private val context: Context) : ApiCallback(context) {

    suspend fun getChallengeInfo(
        catId: String,
        posId: String,
    ) {
        getChallengeDetails(catId, posId) { details ->
            val colorSwatchDao: ColorSwatchDao = MyApplication.getDb().colorSwatchDao()
            val colorSwatchEntity = ColorSwatchEntity()
            colorSwatchEntity.postId = details.id.toInt()
            colorSwatchEntity.swatches = Gson().toJson(details.swatches)
            Executors.newSingleThreadExecutor().execute {
                colorSwatchDao.insertAll(
                    colorSwatchEntity
                )
            }
            processTutorials(details)
        }
    }

    private fun processTutorials(details: PostDetailModel) {
        val tutorial_type = details.calculateTutorialType()
        if (tutorial_type === Tutorial_Type.See_Video) {
            StringConstants.IsFromDetailPage = true
            val intent = Intent(
                context,
                Play_YotubeVideo::class.java
            )
            intent.putExtra("url", details.getYoutube_link_list())
            intent.putExtra("isVideo", true)
            context.startActivity(intent)
            return
        }
        if (tutorial_type === Tutorial_Type.Read_Post) {
            try {
                val link =
                    details.getExternal_link().replace("htttps://", "https://").trim { it <= ' ' }
                KGlobal.openInBrowser(context, link)
            } catch (e: Exception) {
                e.printStackTrace()
                showGenericErrorToast()
            }
            return
        }

        if (tutorial_type === Tutorial_Type.SeeVideo_From_External_Link) {
            StringConstants.IsFromDetailPage = true
            val intent = Intent(
                context,
                Play_YotubeVideo::class.java
            )
            intent.putExtra("url", details.getExternal_link())
            intent.putExtra("isVideo", true)
            context.startActivity(intent)
            return
        }

        if (tutorial_type === Tutorial_Type.Video_Tutorial_Overraid) {
            val fileName: String = details.getVideo_and_file_list()[0].obj_overlaid.getFilename()
            val file = File(KGlobal.getTraceImageFolderPath(context) + "/" + fileName)
            val youtubeLink: String = details.getYoutube_link_list()
            if (youtubeLink.isEmpty()) {
                val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                    .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                openTutorialsRewardPoint(details.id)
                if (!file.exists()) {
                    val downloadsImage = DownloadsImage(context, _youtube_id, details)
                    showProgressDialog()
                    runBlocking {
                        val imagePath = downloadsImage.executeAsyncTask()
                        // Now you have the path to the downloaded image, you can use it as needed
                        hideProgressDialog()
                        if (imagePath != null) {
                            downloadsImage.postExecute(imagePath)
                        } else {
                            showGenericErrorToast()
                        }
                    }
                    return
                } else {
                    StringConstants.IsFromDetailPage = false
                    val intent = Intent(
                        context,
                        PaintActivity::class.java
                    )
                    intent.action = "LoadWithoutTrace"
                    intent.putExtra("path", fileName)
                    intent.putExtra("youtube_video_id", _youtube_id)
                    intent.putExtra("ParentFolderPath", KGlobal.getTraceImageFolderPath(context))
                    if (details.getCanvas_color().isNotEmpty()) {
                        intent.putExtra("canvas_color", details.getCanvas_color())
                    }
                    val swatches: List<ColorSwatch> = details.swatches
                    val gson = Gson()
                    val swatchesJson = gson.toJson(swatches)
                    intent.putExtra("swatches", swatchesJson)
                    intent.putExtra("drawingType", "TUTORAILS")
                    intent.putExtra("id", details.id)
                    context.startActivity(intent)
                    return
                }
            } else {
                showGenericErrorToast("Youtube Link Not Found!")
            }
            return
        }
        if (tutorial_type === Tutorial_Type.READ_POST_DEFAULT) {
            try {
                KGlobal.openInBrowser(context,
                    defaultLink.trim { it <= ' ' })
            } catch (e: Exception) {
                e.printStackTrace()
                showGenericErrorToast()
            }
        }

        if (tutorial_type === Tutorial_Type.DO_DRAWING_OVERLAY) {
            showProgressDialog()
            val downloadOverlayFromDoDrawing = DownloadOverlayFromDoDrawing(context, false, details)
            runBlocking {
                val imagePath = downloadOverlayFromDoDrawing.executeAsyncTask()
                hideProgressDialog()
                if (imagePath != null) {
                    openTutorialsRewardPoint(details.id)
                    downloadOverlayFromDoDrawing.postExecute(imagePath)
                } else {
                    showGenericErrorToast()
                }
            }

            return
        }

        if (tutorial_type === Tutorial_Type.DO_DRAWING_TRACE) {
            showProgressDialog()

            val downloadOverlayFromDoDrawing = DownloadOverlayFromDoDrawing(context, true, details)
            runBlocking {
                val imagePath = downloadOverlayFromDoDrawing.executeAsyncTask()
                hideProgressDialog()
                if (imagePath != null) {
                    openTutorialsRewardPoint(details.id)
                    downloadOverlayFromDoDrawing.postExecute(imagePath)
                } else {
                    showGenericErrorToast()
                }
            }

            return
        }

        if (tutorial_type === Tutorial_Type.Video_Tutorial_Trace) {
            try {
                val youtubeLink: String = details.getYoutube_link_list()
                if (youtubeLink.isEmpty()) {
                    val _youtube_id = youtubeLink.replace("https://youtu.be/", "")
                        .replace("?list=PLJeIp0p4p-igNMVFp6PabvFOX_e0IoIxz", "")
                    if (details.getVideo_and_file_list() != null && details.getVideo_and_file_list()
                            .get(0).obj_trace_image != null && details.getVideo_and_file_list()
                            .get(0).obj_trace_image.getObj_sizes() != null
                    ) {
                        if (details.getVideo_and_file_list().get(0).obj_trace_image.getObj_sizes()
                                .getLarge() != null
                        ) {
                            val fileName: String = details.getVideo_and_file_list()
                                .get(0).obj_trace_image.getObj_sizes().getLarge().substring(
                                    details.getVideo_and_file_list()
                                        .get(0).obj_trace_image.getObj_sizes().getLarge()
                                        .lastIndexOf('/') + 1
                                )
                            val file =
                                File(KGlobal.getTraceImageFolderPath(context) + "/" + fileName)
                            if (!file.exists()) {
                                val downloadsImage =
                                    DownloadsImage(context, _youtube_id, details, true)
                                showProgressDialog()
                                runBlocking {
                                    val imagePath = downloadsImage.executeAsyncTask()
                                    // Now you have the path to the downloaded image, you can use it as needed
                                    hideProgressDialog()
                                    if (imagePath != null) {
                                        downloadsImage.postExecute(imagePath)
                                    } else {
                                        showGenericErrorToast()
                                    }
                                }
                            } else {
                                openTutorialsRewardPoint(details.id)
                                StringConstants.IsFromDetailPage = false
                                val intent = Intent(
                                    context,
                                    PaintActivity::class.java
                                )
                                intent.putExtra("youtube_video_id", _youtube_id)
                                intent.action = "YOUTUBE_TUTORIAL"
                                intent.putExtra("drawingType", "TUTORAILS")
                                intent.putExtra("paint_name", file.absolutePath)
                                if (!details.getCanvas_color().isEmpty()) {
                                    intent.putExtra("canvas_color", details.getCanvas_color())
                                }
                                intent.putExtra("id", details.getID())
                                context.startActivity(intent)
                            }
                        }
                    } else {
                        openTutorialsRewardPoint(details.id)
                        StringConstants.IsFromDetailPage = false
                        val intent = Intent(context, PaintActivity::class.java)
                        intent.putExtra("youtube_video_id", _youtube_id)
                        intent.action = "YOUTUBE_TUTORIAL"
                        intent.putExtra("drawingType", "TUTORAILS")
                        if (!details.getCanvas_color().isEmpty()) {
                            intent.putExtra("canvas_color", details.getCanvas_color())
                        }
                        intent.putExtra("id", details.getID())
                        context.startActivity(intent)
                    }
                }
            } catch (e: java.lang.Exception) {
                showGenericErrorToast()
            }
        }

        if (tutorial_type === Tutorial_Type.Strokes_Overlaid_Window) {

            showProgressDialog()
            val downloadOverlayImage = DownloadOverlayImage(context, details)

            runBlocking {
                val filesList = downloadOverlayImage.executeAsyncTask()
                hideProgressDialog()
                if (filesList != null) {
                    downloadOverlayImage.postExecute(filesList)
                } else {
                    showGenericErrorToast()
                }
            }
        }
    }


    private fun openTutorialsRewardPoint(tId: String) {
        FirebaseFirestoreApi.claimActivityPointsWithId(StringConstants.open_tutorial, tId)
     /*   if (FirebaseAuth.getInstance().currentUser != null) {
            val rewardSetup = AppUtils.getRewardSetup(context)
            if (rewardSetup != null) {
                updateIncreasableRewardValue(
                    "opening_tutorials",
                    rewardSetup.opening_tutorials ?: 0,
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            }
        }*/
    }
}