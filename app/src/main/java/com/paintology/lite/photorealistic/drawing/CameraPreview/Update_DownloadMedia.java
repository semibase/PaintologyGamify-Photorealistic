package com.paintology.lite.photorealistic.drawing.CameraPreview;


public interface Update_DownloadMedia {

//    public void StartDownloading(ChatObject object);

//    public void DownloadCompleted(boolean isDone, ReceivedGroupMessage MessageObjects, boolean isImage);

    public void DownloadCompleted(boolean isDone, ImageManageModel LocalImageObj);

    public void DownloadCaptureCompleted(boolean isDone, ImageManageModel LocalImageObj, Integer FileType);
}
