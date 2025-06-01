package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.Random;

public class DashPathLine extends Brush {
    private String TAG;
    private Random mRandom;

    StringConstants _constant = new StringConstants();

    public DashPathLine() {
        Random lRandom = new Random();
        mRandom = lRandom;
        TAG = "LineBrush";
        int i = mRandom.nextInt(256);
        mBrushGreyValue = i;

      /*  mBrushMaxSize = 25.0F;
        mBrushMinSize = 0.5F;*/

        int screenWidth = _constant.getInt(_constant._scree_width, MyApplication.getInstance());
        int screenHeight = _constant.getInt(_constant._scree_height, MyApplication.getInstance());
        int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            if (screenHeight <= 1024) {
                mBrushMaxSize = 125.0F;
            } else if (screenHeight > 1024 && screenHeight <= 2000) {
                mBrushMaxSize = 225.0f;
            } else {
                mBrushMaxSize = 325.0f;
            }
        } else {
            // code for landscape mode
            if (screenWidth <= 1024) {
                mBrushMaxSize = 125.0F;
            } else if (screenWidth > 1024 && screenWidth <= 2000) {
                mBrushMaxSize = 225.0f;
            } else {
                mBrushMaxSize = 325.0f;
            }
        }

        mBrushMinSize = 0.1f;
        mBrushSize = 5.0F;
        mSizeBias = 1.5F;

        mBrushHasAlpha = true;
        mHasGlobalAlpha = true;
        mBrushStyle = 48;
        mBrushMode = 17;
        mMustRedrawWholeStrokePath = false;
        mIsRandomColor = true;
        mBrushArchiveDataSize = 3;

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
        float f1 = (pRandom.nextInt(5) + -2) * 0.6F;
        float f2 = pFloat + f1;
        float f3 = mSizeLowerBound;
        float f4 = Math.max(f2, f3);
        float f5 = mSizeUpperBound;
        return Math.min(f4, f5);
    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[3];
        float f1 = mBrushSize;
        arrayOfFloat[0] = f1;
        float f2 = mBrushAlphaValue;
        arrayOfFloat[1] = f2;
        float f3 = mBrushGreyValue;
        arrayOfFloat[2] = f3;
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
        if ((mBrushMode == 17) && (mRandomColorPicker != null)) {
            int i = mRandomColorPicker.getRandomColor();
            mBrushColor = i;
        }
        preparePaint();
    }

    Paint lPaint5;

    public void preparePaint() {
//        mBrushPaint.setAntiAlias(true);
        Log.e(TAG, "Prepare Paint called mBrushSize " + mBrushSize);
        /*lPaint5 = mBrushPaint;
        int i = mBrushColor;
        lPaint5.setAntiAlias(true);
        lPaint5.setDither(true);
        lPaint5.setStrokeJoin(Paint.Join.ROUND);
        lPaint5.setStrokeCap(Paint.Cap.ROUND);
        lPaint5.setStyle(Paint.Style.STROKE);
        lPaint5.setStrokeWidth(mBrushSize);
        lPaint5.setPathEffect(new ComposePathEffect(
                new DashPathEffect(new float[]{mBrushSize * 0.8f, mBrushSize * 0.2f}, 0),
                new CornerPathEffect(mBrushSize * 0.2f)));
        lPaint5.setColor(i);*/

        lPaint5 = new Paint();
        lPaint5 = mBrushPaint;
        lPaint5.setAntiAlias(true);
        lPaint5.setDither(true);
        lPaint5.setColor(mBrushColor);
        lPaint5.setStyle(Paint.Style.STROKE);
        lPaint5.setStrokeJoin(Paint.Join.ROUND);
        lPaint5.setStrokeCap(Paint.Cap.ROUND);
        lPaint5.setStrokeWidth(mBrushSize);
        lPaint5.setFilterBitmap(true);
        lPaint5.setPathEffect(new DashPathEffect(new float[]{15, mBrushSize + 5.0f}, 0));
//        lPaint5.setPathEffect(new DashPathEffect(new float[]{mBrushSize, mBrushSize + 5.0f, mBrushSize, mBrushSize + 5.0f}, 1.0f));
    }


    public void randomPaint(Paint pPaint, Random pRandom) {
        preparePaint();
    }

    public void restoreBrush(float[] pArrayOfFloat) {
//        Log.e(TAG, "restoreBrush called");
        mBrushSize = pArrayOfFloat[0];
        mBrushAlphaValue = (int) pArrayOfFloat[1];
        mBrushGreyValue = (int) pArrayOfFloat[2];
        restorePaint();
    }

    public void restorePaint() {
        preparePaint();
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
        drawLine(pCanvas, pPoint1, pPoint2);
    }

    public void updateBrush() {
        Log.e("TAG", "Update LineBrush called Line " + mBrushSize);
        /*mBrushSize = (float) getsize();
        Log.e("TAG", "Update Brush called Line " + mBrushSize + " " + BrushPickerActivity.getmBrushSize());
        preparePaint();*/
    }


   /* double randomSize = 0.5f;

    boolean isReachToEdge = false;
    float maxSize = 0;

    public double getsize() {


        if (BrushPickerActivity.getmBrushSize() >= 3) {
            maxSize = BrushPickerActivity.getmBrushSize() - 3f;
        } else
            maxSize = BrushPickerActivity.getmBrushSize();

        if (randomSize >= maxSize) {
            isReachToEdge = true;
        } else if (randomSize <= 1) {
            isReachToEdge = false;
        }

        if (!isReachToEdge) {
            randomSize += getSlop();
        } else
            randomSize -= getSlop();

        Log.e("TAG", "Random Size " + randomSize + " slop " + getSlop());
        return randomSize;
    }


    public float getSlop() {
        if (BrushPickerActivity.getmBrushSize() <= 10) {
            return 0.5f;
        } else if (BrushPickerActivity.getmBrushSize() >= 10 && BrushPickerActivity.getmBrushSize() <= 15) {
            return 0.6f;
        } else if (BrushPickerActivity.getmBrushSize() >= 15 && BrushPickerActivity.getmBrushSize() <= 20) {
            return 0.7f;
        } else
            return 0.8f;
    }*/
}
