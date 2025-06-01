package com.paintology.lite.photorealistic.drawing.gallery;

public class model_DownloadedTutorial {


    private String downloadedFilePath = "";
    private String DownloadedFileName = "";
    private String DownloadedStrokeFilePath = "";
    private String DownloadedEevntFilePath = "";
    private String DownloadedVideoURL = "";

    private Boolean isSelected = false;

    public String getDownloadedFilePath() {
        return downloadedFilePath;
    }

    public void setDownloadedFilePath(String downloadedFilePath) {
        this.downloadedFilePath = downloadedFilePath;
    }

    public String getDownloadedFileName() {
        return DownloadedFileName;
    }

    public void setDownloadedFileName(String downloadedFileName) {
        DownloadedFileName = downloadedFileName;
    }

    public String getDownloadedStrokeFilePath() {
        return DownloadedStrokeFilePath;
    }

    public void setDownloadedStrokeFilePath(String downloadedStrokeFilePath) {
        DownloadedStrokeFilePath = downloadedStrokeFilePath;
    }

    public String getDownloadedEevntFilePath() {
        return DownloadedEevntFilePath;
    }

    public void setDownloadedEevntFilePath(String downloadedEevntFilePath) {
        DownloadedEevntFilePath = downloadedEevntFilePath;
    }

    public String getDownloadedVideoURL() {
        return DownloadedVideoURL;
    }

    public void setDownloadedVideoURL(String downloadedVideoURL) {
        DownloadedVideoURL = downloadedVideoURL;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

}
