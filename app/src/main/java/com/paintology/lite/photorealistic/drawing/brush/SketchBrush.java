package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.Random;

public class SketchBrush extends Brush {
    private String TAG = "SketchBrush";
    private Random mRandom;
    StringConstants _constant = new StringConstants();

    public SketchBrush() {
        mRandom = new Random();
        setupBrush(33);
    }

    public SketchBrush(int pInt) {
        mRandom = new Random();
        setupBrush(pInt);
    }

    private void setupBrush(int pInt) {
        mBrushStyle = 64;
        mBrushMode = pInt;

        if (mBrushMode == 17)
            mIsRandomColor = true;
        else
            mIsRandomColor = false;

		/*mBrushMaxSize = 5.0F;
		mBrushMinSize = 0.5F;
		mBrushSize = 1.5F;
		*/

        int screenWidth = _constant.getInt(_constant._scree_width, MyApplication.getInstance());
        int screenHeight = _constant.getInt(_constant._scree_height, MyApplication.getInstance());
        int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            if (screenHeight <= 1024) {
                mBrushMaxSize = 50.0F;
                mBrushMinSize = 0.01F;
            } else if (screenHeight > 1024 && screenHeight <= 2000) {
                mBrushMaxSize = 125.0f;
                mBrushMinSize = 0.01f;
            } else {
                mBrushMaxSize = 175.0f;
                mBrushMinSize = 0.01f;
            }
        } else {
            // code for landscape mode
            if (screenWidth <= 1024) {
                mBrushMaxSize = 50.0F;
                mBrushMinSize = 0.01F;
            } else if (screenWidth > 1024 && screenWidth <= 2000) {
                mBrushMaxSize = 125.0f;
                mBrushMinSize = 0.01f;
            } else {
                mBrushMaxSize = 175.0f;
                mBrushMinSize = 0.01f;
            }
        }

        Log.e("TAG", "Sketch Pen Brush Sizes " + _constant.getInt(_constant._scree_width, MyApplication.getInstance()) + " " + _constant.getInt(_constant._scree_height, MyApplication.getInstance()) + " mBrushMaxSize " + mBrushMaxSize);


        mBrushSize = 1.5F;


        mBrushColor = -16777216;
        mMustRedrawWholeStrokePath = false;
        mBrushAlphaValue = 255;
        mBrushArchiveDataSize = 2;
        mSupportOpacity = false;

        return;
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
        if ((mBrushMode == 17) && (mRandomColorPicker != null)) {
            mBrushColor = mRandomColorPicker.getRandomColor();
        }

        preparePaint();
    }

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);
        mBrushPaint.setStyle(Paint.Style.STROKE);
        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
        mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
//        mBrushColor = -16777216;
        mBrushAlphaValue = 160;
        mBrushPaint.setColor(mBrushColor);
        mBrushPaint.setAlpha(mBrushAlphaValue);
        mBrushPaint.setStrokeWidth(mBrushSize);
    }

    public void randomPaint(Paint pPaint) {
        mBrushSize = randomWidth(mBrushSize);
        pPaint.setStrokeWidth(mBrushSize);
    }

    protected float randomWidth(float pFloat) {
        return Math.min(Math.max(pFloat + (mRandom.nextInt(5) + -2) * 0.4F, mSizeLowerBound), mSizeUpperBound);
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

    public void updateBrush() {
        mBrushPaint.setAlpha(mBrushAlphaValue);
        randomPaint(mBrushPaint);
    }

    @Override
    public Rect drawStroke(Canvas pCanvas, Point pPoint1,
                           Point pPoint2, Point pPoint3) {
        // TODO Auto-generated method stub
        return null;
    }
}
