package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.bezier.QuadCurve;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.ArrayList;

public class SketchyLineBrush extends SketchyBrush {
    private String TAG = "SketchyLineBrush";
    private PointF mCtrlPoint;
    private ArrayList<PointF> mCurvePoints;
    private PointF mEndPoint;
    private PointF mStartPoint;
    StringConstants _constant = new StringConstants();

    public SketchyLineBrush() {
        PointF lPointF1 = new PointF();
        mStartPoint = lPointF1;
        PointF lPointF2 = new PointF();
        mCtrlPoint = lPointF2;
        PointF lPointF3 = new PointF();
        mEndPoint = lPointF3;
        ArrayList lArrayList = new ArrayList();
        mCurvePoints = lArrayList;
        setupBrush(33);
        mHistoryStrokesToConnect = 0;
    /*mBrushMaxSize = 4.0F;
    mBrushMinSize = 1.0F;
    mBrushSize = 1.0F;
    */

       /* mBrushMaxSize = 20.0F;
        mBrushMinSize = 0.01F;
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


        mBrushSize = 1.0F;


        mBrushMaxAlpha = 150;
    }


    public Rect StrokeEnd(Canvas pCanvas, float pFloat1, float pFloat2) {
        return null;
    }

    protected void drawCoreStroke(Canvas pCanvas, float pFloat1, float pFloat2) {
        Paint lPaint1 = mBrushPaint;
        int i = mBrushAlphaValue;
        lPaint1.setAlpha(i);
        mStrokePath.reset();
        Path lPath1 = mStrokePath;
        float f1 = mStartPoint.x;
        float f2 = mStartPoint.y;
        lPath1.moveTo(f1, f2);
        Path lPath2 = mStrokePath;
        float f3 = mCtrlPoint.x;
        float f4 = mCtrlPoint.y;
        float f5 = mEndPoint.x;
        float f6 = mEndPoint.y;
        lPath2.quadTo(f3, f4, f5, f6);
        Path lPath3 = mStrokePath;
        Paint lPaint2 = mBrushPaint;
        pCanvas.drawPath(lPath3, lPaint2);
        Path lPath4 = mStrokePath;
        updateDirtyRect(lPath4);
    }

    protected void drawSideStrokes(Canvas pCanvas, int pInt) {
        float f1 = 1;//mScaledDensity;
        float f2 = 150.0F * f1;
        float f3 = f2 * f2;

//    for (int i = pInt; i > 0; i--)
//    {    
//	    int j = mCurvePoints.size() - i;
//    for (int i = pInt; i > 0; i--)
        {
            int j = mCurvePoints.size() - pInt;

            if (j == 0)
                return;

            int k = j - 150;

            if (k < 0)
                k = 0;

            float f4 = ((PointF) mCurvePoints.get(j)).x;
            float f5 = ((PointF) mCurvePoints.get(j)).y;
            mBrushPaint.setAlpha(mBrushAlphaValue / 3);

            for (int n = k; n <= j; n++) {
                PointF lPointF = (PointF) mCurvePoints.get(n);
                float f6 = lPointF.x - f4;
                float f7 = lPointF.y - f5;
                float f10 = f6 * f6 + f7 * f7;

                if (f10 < f3 /*&& (mRandom.nextFloat() > (4.0F * f10 / f3))*/) {
                    float f13 = f6 * 0.3F;
                    float f14 = f4 + f13;
                    float f15 = f7 * 0.3F;
                    float f16 = f5 + f15;
                    float f17 = lPointF.x;
                    float f18 = f6 * 0.3F;
                    float f19 = f17 - f18;
                    float f20 = lPointF.y;
                    float f21 = f7 * 0.3F;
                    float f22 = f20 - f21;
                    mStrokePath.reset();
                    mStrokePath.moveTo(f14, f16);
                    mStrokePath.lineTo(f19, f22);
                    pCanvas.drawPath(mStrokePath, mBrushPaint);
                    unionDirtyRect(mStrokePath);
                }
            }
        }
    }

    public void finish() {
        mCurvePoints.clear();
    }

    public void prepareBrush() {
        super.prepareBrush();
        Paint lPaint = mBrushPaint;
        float f = mBrushSize;
        lPaint.setStrokeWidth(f);
    }

//  public void restorePaint()
//  {
//    super.restorePaint();
//    Paint lPaint = mBrushPaint;
//    float f = mBrushSize;
//    lPaint.setStrokeWidth(f);
//  }

    public void preparePaint() {
        super.preparePaint();
        Paint lPaint = mBrushPaint;
        float f = mBrushSize;
        lPaint.setStrokeWidth(f);
    }

    protected void setupBrush(int pInt) {
        super.setupBrush(pInt);
        mBrushStyle = 272;
        mBrushAlphaValue = 120;
    }

    public void strokeFrom(float pFloat1, float pFloat2) {
        mPrevPoint.x = pFloat1;
        mPrevPoint.y = pFloat2;
        mStartPoint.set(pFloat1, pFloat2);
        mCtrlPoint.set(pFloat1, pFloat2);
    }

    public Rect strokeTo(Canvas pCanvas, float pFloat1, float pFloat2) {
        PointF lPointF1 = mEndPoint;
        float f1 = (mCtrlPoint.x + pFloat1) / 2.0F;
        lPointF1.x = f1;
        PointF lPointF2 = mEndPoint;
        float f2 = (mCtrlPoint.y + pFloat2) / 2.0F;
        lPointF2.y = f2;
        if (mQuadCurve == null) {
            int i = Log.e(TAG, "no quad tool ");
            QuadCurve lQuadCurve1 = new QuadCurve();
            mQuadCurve = lQuadCurve1;
        }
        mQuadCurve.setThreashold(10.0F);
        QuadCurve lQuadCurve2 = mQuadCurve;
        PointF lPointF3 = mStartPoint;
        PointF lPointF4 = mCtrlPoint;
        PointF lPointF5 = mEndPoint;
        lQuadCurve2.Decompose(lPointF3, lPointF4, lPointF5);
        int j = mQuadCurve.getPointNum();
        PointF[] arrayOfPointF = mQuadCurve.getPoints();
        drawCoreStroke(pCanvas, pFloat1, pFloat2);
        int k = j + -1;
        int m = 0;
        while (true) {
            if (m >= k) {
                drawSideStrokes(pCanvas, k);
                mPrevPoint.x = pFloat1;
                mPrevPoint.y = pFloat2;
                PointF lPointF6 = mStartPoint;
                PointF lPointF7 = mEndPoint;
                lPointF6.set(lPointF7);
                mCtrlPoint.set(pFloat1, pFloat2);
                return mDirtyRect;
            }
            ArrayList lArrayList = mCurvePoints;
            float f3 = arrayOfPointF[m].x;
            float f4 = arrayOfPointF[m].y;
            PointF lPointF8 = new PointF(f3, f4);
            boolean bool = lArrayList.add(lPointF8);
            m += 1;
        }
    }
}
