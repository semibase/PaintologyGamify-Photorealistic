package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.Random;

public class RainbowBrush extends Brush {
    private String TAG;
    protected boolean mBrushHasAlpha;
    protected SweepGradient mRainbow;
    protected Random mRandom;
    StringConstants _constant = new StringConstants();

    public RainbowBrush() {
        Random lRandom = new Random();
        mRandom = lRandom;
        TAG = "RainbowBrush";
        mBrushAlphaValue = 255;

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


        mBrushSize = 6.0F;
        mSizeBias = 2.0F;
        setSizeBound();
        mBrushStyle = 39;
        mBrushMode = 17;
        ;
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
        mIsRandomColor = true;
        mBrushArchiveDataSize = 3;
        mSupportOpacity = false;
    }

    private int randomAlpha(int pInt1, int pInt2, Random pRandom) {
        int i = pInt2 * 2 + 1;
        int j = pRandom.nextInt(i) - pInt2;
        pInt1 += j;
        if (pInt1 <= 100)
            pInt1 = 100;
        if (pInt1 > 255)
            pInt1 = 255;
        return pInt1;
    }

    private float randomWidth(float pFloat, int pInt, Random pRandom) {
        int i = pInt * 2 + 1;
        float f = pRandom.nextInt(i) - pInt;
        pFloat += f;
        if (pFloat <= 0.0F)
            pFloat = 4.0F;
        if (pFloat > 8.0F)
            pFloat = 8.0F;
        return pFloat;
    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[3];
        float f1 = mBrushSize;
        arrayOfFloat[0] = f1;
        float f2 = mBrushPatternWidth;
        arrayOfFloat[1] = f2;
        float f3 = mBrushColor;
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
    }

    public void prepareBrush() {
        Paint lPaint = mBrushPaint;
        Random lRandom = mRandom;
        randomPaint(lPaint, lRandom);
    }

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);
        Paint lPaint1 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.ROUND;
        lPaint1.setStrokeCap(lCap);
        Paint lPaint2 = mBrushPaint;
        Paint.Join lJoin = Paint.Join.ROUND;
        lPaint2.setStrokeJoin(lJoin);
        Log.e("TAG", "preparePaint mBrushHasAlpha " + mBrushHasAlpha);
        if (mBrushHasAlpha) {
            Paint lPaint3 = mBrushPaint;
            int i = mBrushAlphaValue;
            lPaint3.setAlpha(i);
        } else
            mBrushPaint.setAlpha(255);

        Paint lPaint4 = mBrushPaint;
        float f = mBrushSize;
        lPaint4.setStrokeWidth(f);
        Paint lPaint5 = mBrushPaint;
        int j = mBrushColor;
        lPaint5.setColor(j);
        if (mRainbow != null) {
            Paint lPaint6 = mBrushPaint;
            SweepGradient lSweepGradient = mRainbow;
            Shader lShader = lPaint6.setShader(lSweepGradient);
        }
    }

    protected int randomAlpha(int pInt, Random pRandom) {
        int i = pRandom.nextInt(21) + -10;
        pInt += i;
        if (pInt <= 100)
            pInt = 100;
        if (pInt > 255)
            pInt = 255;
        return pInt;
    }

    protected int randomColor(Random pRandom) {
        if (mRandomColorPicker != null)
            return mRandomColorPicker.getNextColor();

        return -65536;
    }

    protected void randomGradient(Random pRandom) {
    }

    public void randomPaint(Paint pPaint, Random pRandom) {
        float f1 = mBrushSize;
        float f2 = randomWidth(f1, pRandom);
        mBrushSize = f2;
        Random lRandom = mRandom;
        int i = randomColor(lRandom);
        mBrushColor = i;
        pPaint.setAlpha(255);
        float f3 = mBrushSize;
        pPaint.setStrokeWidth(f3);
        int j = mBrushColor;
        pPaint.setColor(j);
    }

    protected float randomWidth(float pFloat, Random pRandom) {
        float f1 = (pRandom.nextInt(5) + -2) * 0.4F;
        float f2 = pFloat + f1;
        float f3 = mSizeLowerBound;
        float f4 = Math.max(f2, f3);
        float f5 = mSizeUpperBound;
        return Math.min(f4, f5);
    }

    public void replayDrawLine(Canvas pCanvas, Point pPoint1, Point pPoint2) {
        restorePaint();
        float f1 = pPoint1.x;
        float f2 = pPoint1.y;
        float f3 = pPoint2.x;
        float f4 = pPoint2.y;
        Paint lPaint = mBrushPaint;
        pCanvas.drawLine(f1, f2, f3, f4, lPaint);
        updateDirtyRect(pPoint1, pPoint2);
    }

    public void restoreBrush(float[] pArrayOfFloat) {
        int i = (int) pArrayOfFloat[0];
        mBrushSize = i;
        int j = (int) pArrayOfFloat[1];
        mBrushPatternWidth = j;
        int k = (int) pArrayOfFloat[2];
        mBrushColor = k;
        restorePaint();
    }

    public void restorePaint() {
        mBrushPaint.setAntiAlias(true);
        Paint lPaint1 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.ROUND;
        lPaint1.setStrokeCap(lCap);
        Paint lPaint2 = mBrushPaint;
        Paint.Join lJoin = Paint.Join.ROUND;
        lPaint2.setStrokeJoin(lJoin);
        if (mBrushHasAlpha) {
            mBrushPaint.setAlpha(mBrushAlphaValue);
        } else
            mBrushPaint.setAlpha(255);

        Paint lPaint4 = mBrushPaint;
        float f = mBrushSize;
        lPaint4.setStrokeWidth(f);
        Paint lPaint5 = mBrushPaint;
        int j = mBrushColor;
        lPaint5.setColor(j);
        if (mRainbow != null) {
            mBrushPaint.setShader(mRainbow);
        }
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void updateBrush() {
        Paint lPaint = mBrushPaint;
        Random lRandom = mRandom;
        randomPaint(lPaint, lRandom);
    }

    @Override
    public Rect drawStroke(Canvas pCanvas, Point pPoint1,
                           Point pPoint2, Point pPoint3) {
        // TODO Auto-generated method stub
        return null;
    }
}
