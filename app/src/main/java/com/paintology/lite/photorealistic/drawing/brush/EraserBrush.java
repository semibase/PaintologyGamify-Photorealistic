package com.paintology.lite.photorealistic.drawing.brush;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.paintology.lite.photorealistic.drawing.painting.Point;

public class EraserBrush extends Brush {
    private PorterDuffXfermode mClearMode;
    private PorterDuffXfermode mCopyMode;

    public EraserBrush() {
        mBrushMaxSize = 60.0F;
        mBrushMinSize = 1.0F;
        mBrushSize = 5.0F;
        mBrushHasAlpha = false;
        mBrushStyle = 112;
        mBrushMode = 33;
        mMustRedrawWholeStrokePath = false;
        mIsRandomColor = false;
        mBrushArchiveDataSize = 2;
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mCopyMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mSupportOpacity = false;
    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = mBrushSize;
        arrayOfFloat[1] = mBrushColor;
        return arrayOfFloat;
    }

    public int[] archivePaint() {
        return null;
    }

    public void draw(Canvas pCanvas, Path pPath) {
    }

    public void draw(Canvas pCanvas, Path pPath, int pInt) {
    }

    public void draw(Canvas pCanvas, Point[] pArrayOfPoint) {
    }

    public void draw(Canvas pCanvas, Point[] pArrayOfPoint, int pInt) {
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void prepareBrush() {
        preparePaint();
    }

    public void preparePaint() {
        if (mBrushPaint == null)
            mBrushPaint = new Paint();

        mBrushPaint.setAntiAlias(true);
        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
        mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
        mBrushPaint.setStyle(Paint.Style.STROKE);
        mBrushPaint.setStrokeWidth(mBrushSize);
        mBrushPaint.setColor(mBrushColor);
        mBrushPaint.setXfermode(mCopyMode);
    }

    public void restoreBrush(float[] pArrayOfFloat) {
        mBrushSize = pArrayOfFloat[0];
        mBrushColor = (int) pArrayOfFloat[1];
        restorePaint();
    }

    public void restorePaint() {
        preparePaint();
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    @Override
    public void updateBrush() {
        // TODO Auto-generated method stub

    }
}
