package com.paintology.lite.photorealistic.drawing.brush;

public class BrushMetaData {
    public int mBrushAlphaValue = 255;
    public int mBrushColor;
    public int mBrushDirection;
    public int mBrushFlow;
    public float mBrushSize;
    public int mBrushStyle;
    public long mRandomSeed;
    public int mRepeatDrawTimes;

    public BrushMetaData() {
    }

    public BrushMetaData(Brush pBrush) {
        mBrushStyle = pBrush.mBrushStyle;
        mBrushSize = pBrush.mBrushSize;
        mBrushColor = pBrush.mBrushColor;
        mBrushAlphaValue = pBrush.mBrushAlphaValue;
        mBrushDirection = pBrush.mBrushDirection;
        mBrushFlow = pBrush.mBrushFlow;
        mRandomSeed = pBrush.mRandomSeed;
        mRepeatDrawTimes = pBrush.mRepeatDrawTimes;
    }
}

