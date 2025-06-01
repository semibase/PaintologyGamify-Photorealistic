package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.painting.Stroker;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;


public class SketchyBrush extends Brush {
    private String TAG = "SketchyBrush";
    protected int mPointCount;
    protected Point mPrevPoint;
    protected Path mStrokePath;

    public SketchyBrush() {
        Point lPoint = new Point();
        mPrevPoint = lPoint;
        mPointCount = 0;
        Path lPath = new Path();
        mStrokePath = lPath;
        setupBrush(33);
        mHistoryStrokesToConnect = 15;
    }

    public float[] archiveBrush() {
        return null;
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

    protected void drawCoreStroke(Canvas pCanvas, float pFloat1, float pFloat2) {
        mStrokePath.reset();
        Path lPath1 = mStrokePath;
        float f1 = mPrevPoint.x;
        float f2 = mPrevPoint.y;
        lPath1.moveTo(f1, f2);
        mStrokePath.lineTo(pFloat1, pFloat2);
        Path lPath2 = mStrokePath;
        Paint lPaint = mBrushPaint;
        pCanvas.drawPath(lPath2, lPaint);
        Path lPath3 = mStrokePath;
        updateDirtyRect(lPath3);
    }

    protected void drawSideStrokes(Canvas pCanvas, float pFloat1, float pFloat2) {
        int i = mPaintingStrokeList.size() - 1;
        int j = mHistoryStrokesToConnect;
        int k = i - j;

        if (k < 0)
            k = 0;
        int m = i;

        for (m = i; m >= k; m--) {
            Stroker lStroker;
            int n;

//    	do
            {
                lStroker = (Stroker) mPaintingStrokeList.get(m);
                n = lStroker.getBrushStyle();
            }
//	    while (n != 256);
            if (n != 256)
                continue;
//	    Log.e("lPoint", String.valueOf(lStroker.mPoints.size()));
            for (int i2 = 0; i2 < lStroker.mPoints.size(); i2++) {
                Point lPoint = (Point) lStroker.mPoints.get(i2);
                float f5 = ((lPoint.x - pFloat1) * (lPoint.x - pFloat1)) + ((lPoint.y - pFloat2) * (lPoint.y - pFloat2));
                if (f5 < 4000.0F) {
//	          if (mRandom.nextFloat() > (f5 / 2000.0F))
                    {
                        mStrokePath.reset();
                        mStrokePath.moveTo(pFloat1 + 0.3F * (lPoint.x - pFloat1), pFloat2 + 0.3F * (lPoint.y - pFloat2));
                        mStrokePath.lineTo(lPoint.x - 0.3F * (lPoint.x - pFloat1), lPoint.y - 0.3F * (lPoint.y - pFloat2));

                        pCanvas.drawPath(mStrokePath, mBrushPaint);
                        unionDirtyRect(mStrokePath);
                    }
                }
            }

            break;
        }
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void finish() {
    }


    public void prepareBrush() {
        if (mBrushMode == 17) {
            if (mRandomColorPicker == null)
                Log.e(TAG, "no random color picker");
            else
                mBrushColor = mRandomColorPicker.getRandomColor();
        }

        mBrushPaint.setAntiAlias(true);
        mBrushPaint.setStyle(Paint.Style.STROKE);
        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
        mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
        mBrushPaint.setColor(mBrushColor);
        mBrushPaint.setAlpha(mBrushAlphaValue);
        mBrushPaint.setStrokeWidth(mBrushSize);
        mRandom.setSeed(mRandomSeed);
    }

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);
        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
        mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
        mBrushPaint.setStyle(Paint.Style.STROKE);
        mBrushPaint.setColor(mBrushColor);
        mBrushPaint.setAlpha(mBrushAlphaValue);
        mBrushPaint.setStrokeWidth(mBrushSize);
        mRandom.setSeed(mRandomSeed);
    }

//  public void restorePaint()
//  {
//    mBrushPaint.setAntiAlias(true);
//    mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
//    mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
//    mBrushPaint.setStyle(Paint.Style.STROKE);
//    mBrushPaint.setColor(mBrushColor);
//    mBrushPaint.setAlpha(mBrushAlphaValue);
//    mBrushPaint.setStrokeWidth(mBrushSize);
//    mRandom.setSeed(mRandomSeed);
//  }

    public void restoreBrush(float[] pArrayOfFloat) {
        mBrushPaint.setAntiAlias(true);
        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
        mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
        mBrushPaint.setStyle(Paint.Style.STROKE);
        mBrushPaint.setColor(mBrushColor);
        mBrushPaint.setAlpha(mBrushAlphaValue);
        mBrushPaint.setStrokeWidth(mBrushSize);
        mRandom.setSeed(mRandomSeed);
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    StringConstants _constant = new StringConstants();

    protected void setupBrush(int pInt) {
        mBrushStyle = 256;
        mBrushMode = pInt;

        if (mBrushMode == 17)
            mIsRandomColor = true;
        else
            mIsRandomColor = false;

        /*mBrushMaxSize = 8.0F;
        mBrushMinSize = 1.0F;
        mBrushSize = 1.0F;
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

        mBrushSize = 1.0F;


//        mBrushColor = -16777216;
        mMustRedrawWholeStrokePath = false;
        mBrushArchiveDataSize = 2;
        mBrushAlphaValue = 70;
        mBrushMaxAlpha = 150;
        mRandomSeed = System.currentTimeMillis();
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public Rect strokeEnd(Canvas pCanvas, float pFloat1, float pFloat2) {
        return new Rect();
    }

    public void strokeFrom(float pFloat1, float pFloat2) {
        mPrevPoint.x = pFloat1;
        mPrevPoint.y = pFloat2;
    }

    public Rect strokeTo(Canvas pCanvas, float pFloat1, float pFloat2) {
        drawCoreStroke(pCanvas, pFloat1, pFloat2);
        drawSideStrokes(pCanvas, pFloat1, pFloat2);
        mPrevPoint.x = pFloat1;
        mPrevPoint.y = pFloat2;
        return mDirtyRect;
    }

    public void updateBrush() {
    }
}
