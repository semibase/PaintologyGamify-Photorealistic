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

public class SketchSingleLineBrush extends SketchyBrush {
    private String TAG = "Sketch Line Brush";
    private Point mCtrlPoint;
    private Point mEndPoint;
    private Point mStartPoint;
    StringConstants _constant = new StringConstants();

    public SketchSingleLineBrush() {
        mStartPoint = new Point();
        mCtrlPoint = new Point();
        mEndPoint = new Point();
        setupBrush(33);
    }

    public SketchSingleLineBrush(int pInt) {
        mStartPoint = new Point();
        mCtrlPoint = new Point();
        mEndPoint = new Point();
        setupBrush(pInt);
		/*mBrushMaxSize = 60.0F;
		mBrushMinSize = 1.0F;
		mBrushSize = 1.0F;
		*/
        mBrushMaxSize = 60.0F;
        mBrushMinSize = 0.5F;
        mBrushSize = 1.5F;

    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[1];
        arrayOfFloat[0] = mBrushSize;
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

    protected void drawCoreStroke(Canvas pCanvas, float pFloat1, float pFloat2) {
        mStrokePath.reset();
        mStrokePath.moveTo(mStartPoint.x, mStartPoint.y);
        mStrokePath.quadTo(mCtrlPoint.x, mCtrlPoint.y, mEndPoint.x, mEndPoint.y);
        pCanvas.drawPath(mStrokePath, mBrushPaint);
        updateDirtyRect(mStrokePath);
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void prepareBrush() {
        if (mBrushMode == 17) {
            if (mRandomColorPicker == null)
                Log.e(TAG, "no random color picker");

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

    public void randomPaint(Paint pPaint) {
        mBrushSize = randomWidth(mBrushSize);
        pPaint.setStrokeWidth(mBrushSize);
    }

    protected float randomWidth(float pFloat) {
        float f1 = (mRandom.nextInt(5) + -2) * 0.4F;
        float f2 = pFloat + f1;
        float f3 = mSizeLowerBound;
        float f4 = Math.max(f2, f3);
        float f5 = mSizeUpperBound;

        return Math.min(f4, f5);
    }

    public void restoreBrush(float[] pArrayOfFloat) {
        mBrushSize = pArrayOfFloat[0];
        mBrushPaint.setStrokeWidth(mBrushSize);
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    protected void setupBrush(int pInt) {
        mBrushStyle = 265;
        mBrushMode = pInt;

        if (mBrushMode == 17)
            mIsRandomColor = true;
        else
            mIsRandomColor = false;

       /* mBrushMaxSize = 60.0F;
        mBrushMinSize = 0.5F;*/
        mBrushSize = 1.0F;
        mBrushColor = -16777216;
        mMustRedrawWholeStrokePath = false;
        mBrushAlphaValue = 200;
        mBrushAlphaValue = 200;
        mBrushAlphaValue = 150;
        mBrushArchiveDataSize = 1;


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

    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void strokeFrom(float pFloat1, float pFloat2) {
        mPrevPoint.x = pFloat1;
        mPrevPoint.y = pFloat2;
        mStartPoint.set(pFloat1, pFloat2);
        mCtrlPoint.set(pFloat1, pFloat2);
    }

    public Rect strokeTo(Canvas pCanvas, float pFloat1, float pFloat2) {
        mEndPoint.x = (mCtrlPoint.x + pFloat1) / 2.0F;
        mEndPoint.y = (mCtrlPoint.y + pFloat2) / 2.0F;
        drawCoreStroke(pCanvas, pFloat1, pFloat2);
        mPrevPoint.x = pFloat1;
        mPrevPoint.y = pFloat2;
        mStartPoint.set(mEndPoint);
        mCtrlPoint.set(pFloat1, pFloat2);
        return mDirtyRect;
    }

    public void updateBrush() {
        mBrushPaint.setAlpha(mBrushAlphaValue);
        randomPaint(mBrushPaint);
    }
}