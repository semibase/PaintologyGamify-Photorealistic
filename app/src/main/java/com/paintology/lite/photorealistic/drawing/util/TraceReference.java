package com.paintology.lite.photorealistic.drawing.util;

import com.paintology.lite.photorealistic.drawing.Enums.drawing_type;

public class TraceReference {

    public String userPaintingName = "", traceImageName = "";

    public boolean isFromPaintologyFolder = false;

    public drawing_type _drawing_type = drawing_type.TraceCanvasDrawing;

    public drawing_type get_drawing_type() {
        return _drawing_type;
    }

    public void set_drawing_type(drawing_type _drawing_type) {
        this._drawing_type = _drawing_type;
    }

    public boolean isFromPaintologyFolder() {
        return isFromPaintologyFolder;
    }

    public void setFromPaintologyFolder(boolean fromPaintologyFolder) {
        isFromPaintologyFolder = fromPaintologyFolder;
    }

    public String getUserPaintingName() {
        return userPaintingName;
    }

    public void setUserPaintingName(String userPaintingName) {
        this.userPaintingName = userPaintingName;
    }

    public String getTraceImageName() {
        return traceImageName;
    }

    public void setTraceImageName(String traceImageName) {
        this.traceImageName = traceImageName;
    }

    public boolean isGrayScale = false;

    public boolean isGrayScale() {
        return isGrayScale;
    }

    public void setGrayScale(boolean grayScale) {
        isGrayScale = grayScale;
    }
}
