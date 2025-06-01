package com.paintology.lite.photorealistic.drawing.Model;

import android.graphics.Bitmap;

public class BrushDemoItem {

    public Bitmap brushDemoImage;
    public int brushIconId;
    public int brushName;
    public int brushType;
    public float[] points;

    public BrushDemoItem(int pInt1, int pInt2, int pBitmap, Bitmap pArrayOfFloat, float[] arg6) {
        brushType = pInt1;
        brushName = pInt2;
        brushIconId = pBitmap;
        brushDemoImage = pArrayOfFloat;
        points = arg6;
    }
}
