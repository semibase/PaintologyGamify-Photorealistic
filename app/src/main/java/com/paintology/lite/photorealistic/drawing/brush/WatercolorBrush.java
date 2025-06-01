package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.Random;

public class WatercolorBrush extends Brush {
    private String TAG;
    private int mMaxAlpha;
    private int mMinAlpha;
    protected Random mRandom;

    StringConstants _constant = new StringConstants();

    public WatercolorBrush(int pInt) {
        Random lRandom = new Random();
        mRandom = lRandom;
        TAG = "WatercolorBrush";
        /*mMinAlpha = 40;
        mMaxAlpha = 80;
        mBrushMaxSize = 40.0F;
        mBrushMinSize = 4.0F;
        mBrushSize = 15.0F;
        mBrushAlphaValue = 80;
        */
        mMinAlpha = 40;
        mMaxAlpha = 80;

        /*mBrushMaxSize = 40.0F;
        mBrushMinSize = 1.0F;
        */
        int screenWidth = _constant.getInt(_constant._scree_width, MyApplication.getInstance());
        int screenHeight = _constant.getInt(_constant._scree_height, MyApplication.getInstance());
        int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            if (screenHeight <= 1024) {
                mBrushMaxSize = 125.0F;
                mBrushMinSize = 0.01F;
            } else if (screenHeight > 1024 && screenHeight <= 2000) {
                mBrushMaxSize = 225.0f;
                mBrushMinSize = 0.01f;
            } else {
                mBrushMaxSize = 325.0f;
                mBrushMinSize = 0.01f;
            }
        } else {
            // code for landscape mode
            if (screenWidth <= 1024) {
                mBrushMaxSize = 125.0F;
                mBrushMinSize = 0.01F;
            } else if (screenWidth > 1024 && screenWidth <= 2000) {
                mBrushMaxSize = 225.0f;
                mBrushMinSize = 0.01f;
            } else {
                mBrushMaxSize = 325.0f;
                mBrushMinSize = 0.01f;
            }
        }
        mBrushSize = 0.1F;
        mBrushAlphaValue = 80;


        mBrushHasAlpha = true;
        mBrushStyle = 55;
        mMustRedrawWholeStrokePath = false;
        mBrushPaint.setAntiAlias(true);
        Paint lPaint1 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.BUTT;
        lPaint1.setStrokeCap(lCap);
        Paint lPaint2 = mBrushPaint;
        Paint.Style lStyle = Paint.Style.STROKE;
        lPaint2.setStyle(lStyle);
        Paint lPaint3 = mBrushPaint;
        float f = mBrushSize;
        lPaint3.setStrokeWidth(f);
        if (pInt == 17) {
            mBrushMode = 17;
            mIsRandomColor = true;
        } else {
            mBrushMode = 33;
            mIsRandomColor = false;
        }

        mSupportOpacity = false;
    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[1];
        float f = mBrushAlphaValue;
        arrayOfFloat[0] = f;
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
        if (mBrushMode == 17) {
            Random lRandom1 = mRandom;
            int i = randomColor(lRandom1);
            mBrushColor = i;
        }
        int j = mBrushAlphaValue;
        Random lRandom2 = mRandom;
        int k = randomAlpha(j, lRandom2);
        mBrushAlphaValue = k;
        preparePaint();
    }

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);
        Paint lPaint1 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.BUTT;
        lPaint1.setStrokeCap(lCap);
        Paint lPaint2 = mBrushPaint;
        Paint.Style lStyle = Paint.Style.STROKE;
        lPaint2.setStyle(lStyle);
        Paint lPaint3 = mBrushPaint;
        float f = mBrushSize;
        lPaint3.setStrokeWidth(f);
        Paint lPaint4 = mBrushPaint;
        int i = mBrushColor;
        lPaint4.setColor(i);
        Paint lPaint5 = mBrushPaint;
        int j = mBrushAlphaValue;
        lPaint5.setAlpha(j);
    }

    protected int randomAlpha(int pInt, Random pRandom) {
        int i = pRandom.nextInt(21) + -10;
        int j = pInt + i;
        int k = mMinAlpha;
        int m = Math.max(j, k);
        int n = mMaxAlpha;
        return Math.min(m, n);
    }

    protected int randomColor(Random pRandom) {
        if (mRandomColorPicker != null) ;
        for (int i = mRandomColorPicker.getRandomColor(); ; i = -65536)
            return i;
    }

    protected float randomWidth(float pFloat, Random pRandom) {
        float f1 = (pRandom.nextInt(5) + -2) * 0.4F;
        float f2 = pFloat + f1;
        float f3 = mBrushMaxSize;
        float f4 = Math.min(f2, f3);
        float f5 = mBrushMinSize;
        return Math.max(f4, f5);
    }

    public void restoreBrush(float[] pArrayOfFloat) {
        int i = (int) pArrayOfFloat[0];
        mBrushAlphaValue = i;
        preparePaint();
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void updateBrush() {
    }
}
