package com.paintology.lite.photorealistic.drawing.CameraPreview;

public class ImageManageModel {

    boolean isDone = false;
    String localImagePath = "";

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }
}
