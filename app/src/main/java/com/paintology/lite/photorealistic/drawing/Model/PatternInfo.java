package com.paintology.lite.photorealistic.drawing.Model;

import android.graphics.Bitmap;

public class PatternInfo {
    public float alphaScale;
    public int maxSize;
    public int minSize;
    public int spacing;
    public int style;
    public String strName = "";
    public Bitmap brushDemoImage = null;
    public boolean isselect = false;
    public String _filePath;
    public float[] points;
    public BrushType _brushType;
    public boolean isPatternBrush;
    public int _index = 0;
    public int resID;

    public PatternInfo(int brush_style, int _nax_size, int _min_size, int _spacing, float _scale, String strName, boolean isselect, int resID, String _filePath, BrushType _brushType, float[] points, boolean isPatternBrush, int _index) {
        style = brush_style;
        maxSize = _nax_size;
        minSize = _min_size;
        spacing = _spacing;
        alphaScale = _scale;
        this.strName = strName;
        this.isselect = isselect;
        this._filePath = _filePath;
        this._brushType = _brushType;
        this.points = points;
        this.isPatternBrush = isPatternBrush;
        this._index = _index;
        this.resID = resID;
    }


    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public Bitmap getBrushDemoImage() {
        return brushDemoImage;
    }

    public void setBrushDemoImage(Bitmap brushDemoImage) {
        this.brushDemoImage = brushDemoImage;
    }
}