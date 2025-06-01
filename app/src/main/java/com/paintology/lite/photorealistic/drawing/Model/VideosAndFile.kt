package com.paintology.lite.photorealistic.drawing.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VideosAndFile {
    @SerializedName("text_file")
    @Expose
    var textFile: TextFile? = null

    @SerializedName("trace_image")
    @Expose
    var traceImage: TraceImage? = null

    @SerializedName("overlay_image")
    @Expose
    var overlayImage: OverlayImage? = null

    @SerializedName("upload_videos")
    @Expose
    var uploadVideos: UploadVideos? = null
}