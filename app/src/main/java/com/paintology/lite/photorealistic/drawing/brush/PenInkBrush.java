package com.paintology.lite.photorealistic.drawing.brush;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;

import java.util.Random;

public class PenInkBrush extends Brush {
    private String TAG;
    private Random mRandom;

    public PenInkBrush() {
        Random lRandom = new Random();
        mRandom = lRandom;
        TAG = "PenInkBrush";
        int i = mRandom.nextInt(256);
        mBrushGreyValue = i;
    /*mBrushMaxSize = 15.0F;
    mBrushMinSize = 1.0F;
    mBrushSize = 3.5F;
    mSizeBias = 1.5F;
    */

        mBrushMaxSize = 15.0F;
        mBrushMinSize = 0.01F;


        mBrushSize = 3.5F;
        mSizeBias = 1.5F;


        setSizeBound();
        mBrushHasAlpha = false;
        mHasGlobalAlpha = true;
        mBrushColor = -16777216;
        mBrushStyle = 41;
        mBrushMode = 17;
        mMustRedrawWholeStrokePath = false;
        mIsRandomColor = true;
        mBrushArchiveDataSize = 1;
    }

    private int randomAlpha(int pInt1, int pInt2, Random pRandom) {
        int i = pInt2 * 2 + 1;
        int j = pRandom.nextInt(i) - pInt2;
        pInt1 += j;
        if (pInt1 <= 100)
            pInt1 = 100;
        if (pInt1 > 220)
            pInt1 = 220;
        return pInt1;
    }

    private int randomAlpha(int pInt, Random pRandom) {
        int i = pRandom.nextInt(21) + -10;
        pInt += i;
        if (pInt <= 100)
            pInt = 100;
        if (pInt > 250)
            pInt = 250;
        return pInt;
    }

    private float randomWidth(float pFloat, int pInt, Random pRandom) {
        int i = pInt * 2 + 1;
        float f1 = pRandom.nextInt(i) - pInt;
        pFloat += f1;
        float f2 = mBrushMinSize;
        if (pFloat <= f2)
            pFloat = mBrushMaxSize / 2.0F;
        float f3 = mBrushMaxSize;
        if (pFloat > f3)
            pFloat = mBrushMaxSize;
        return pFloat;
    }

    private float randomWidth(float pFloat, Random pRandom) {
        float f1 = (pRandom.nextInt(5) + -2) * 0.9F;
        float f2 = pFloat + f1;
        float f3 = mSizeLowerBound;
        float f4 = Math.max(f2, f3);
        float f5 = mSizeUpperBound;
        return Math.min(f4, f5);
    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[1];
        float f = mBrushSize;
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
        drawLine(pCanvas, pPoint1, pPoint2);
    }

    public void prepareBrush() {
        preparePaint();
    }

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);
        Paint lPaint1 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.ROUND;
        lPaint1.setStrokeCap(lCap);
        Paint lPaint2 = mBrushPaint;
        Paint.Join lJoin = Paint.Join.ROUND;
        lPaint2.setStrokeJoin(lJoin);
        Paint lPaint3 = mBrushPaint;
        Paint.Style lStyle = Paint.Style.STROKE;
        lPaint3.setStyle(lStyle);
        Paint lPaint4 = mBrushPaint;
        float f = mBrushSize;
        lPaint4.setStrokeWidth(f);
        Paint lPaint5 = mBrushPaint;
        int i = mBrushColor;
        lPaint5.setColor(i);
    }

    public void randomPaint(Paint pPaint, Random pRandom) {
        Log.e("TAG", "Random Paint mBrushSize " + mBrushSize);
        float f1 = mBrushSize;
        float f2 = randomWidth(f1, pRandom);
        Log.e("TAG", "Random Paint f2 " + f2);
        mBrushSize = f2;
        preparePaint();
    }

    public void restoreBrush(float[] pArrayOfFloat) {
        mBrushSize = pArrayOfFloat[0];
        restorePaint();
    }

    public void restorePaint() {
        preparePaint();
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void setColor(int pInt) {
        mBrushColor = pInt;
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
        drawLine(pCanvas, pPoint1, pPoint2);
    }

    public void updateBrush() {
        Log.e("TAG", "Update Brush Called");
        Paint lPaint = mBrushPaint;
        Random lRandom = mRandom;
        randomPaint(lPaint, lRandom);
    }
}
