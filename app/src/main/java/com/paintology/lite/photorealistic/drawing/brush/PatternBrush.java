package com.paintology.lite.photorealistic.drawing.brush;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.bezier.QuadCurve;
import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.pattern.PatternManager;

import java.util.Random;

public class PatternBrush extends Brush {
    private String TAG;
    PointF cp;
    PointF ep;
    private int mAdjustedAlpha;
    Rect mBound;
    protected Random mRandom;
    public int mSpacing;
    PointF sp;

    public PatternBrush(int pInt, PatternManager pPatternManager) {
        mRandom = new Random();
        mBound = new Rect();
        sp = new PointF();
        cp = new PointF();
        ep = new PointF();
        TAG = "PatternBrush";
        mBrushStyle = pInt;
        mBrushOriginalSize = 20.0F;
        mBrushSize = 50.0F;
        mMustRedrawWholeStrokePath = false;

        if (pPatternManager != null) {
            mPatternManager = pPatternManager;
//            mBrushMaxSize = pPatternManager.getMaxSize(pInt);
//            mBrushMinSize = pPatternManager.getMinSize(pInt);
        }

        /*mBrushMaxSize = 350.0f;
        mBrushMinSize = 10.0f;*/

        mHasGlobalAlpha = true;
        mSupportFlow = true;
        mBrushAlphaValue = 50;
        mAdjustedAlpha = 50;
    }

    private void loadPattern() {
        if (mBrushPattern == null) {
            mBrushPattern = mPatternManager.getPattern(mBrushStyle, (int) mBrushSize, mBrushColor);
            mBrushPatternWidth = mBrushPattern.getWidth();
            mBrushPatternHeight = mBrushPattern.getHeight();
            mBound.set(0, 0, mBrushPatternWidth, mBrushPatternHeight);
        }
    }

    protected void MyDbgLog(String pString1, String pString2) {
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

    public Rect drawStroke(Canvas pCanvas, Point pPoint1, Point pPoint2, Point pPoint3) {
        sp.set(pPoint1.x, pPoint1.y);
        cp.set(pPoint2.x, pPoint2.y);
        ep.set(pPoint3.x, pPoint3.y);

        if (mQuadCurve == null) {
            Log.e(TAG, "no quad tool ");
            mQuadCurve = new QuadCurve();
        }

        mSpacing = mPatternManager.getSpacing(mBrushStyle, (int) mBrushSize);
        mQuadCurve.setThreashold(mSpacing);
        mQuadCurve.Decompose(sp, cp, ep);

        PointF[] arrayOfPointF = mQuadCurve.getPoints();
        mDirtyRect.setEmpty();
        loadPattern();

        for (int i1 = 0; i1 < mQuadCurve.getPointNum(); i1++) {
            int i2 = (int) (arrayOfPointF[i1].x - mBrushPatternWidth / 4);
            int i3 = (int) (arrayOfPointF[i1].y - mBrushPatternHeight / 4);
            if (mBrushPattern != null && !mBrushPattern.isRecycled()) {
                pCanvas.drawBitmap(mBrushPattern, i2, i3, mBrushPaint);
                mBound.offsetTo(i2, i3);
                mDirtyRect.union(new Rect(mBound.left - 1, mBound.top - 1, mBound.right + 1, mBound.bottom + 1));
            }
        }
        return mDirtyRect;
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void endStroke() {
        mBrushPattern = null;
    }

    public void prepareBrush() {
        if (mBrushMode == 17) {
            mBrushColor = randomColor();
        }
        preparePaint();
    }

    public void preparePaint() {
        int k = mPatternManager.getSpacing(mBrushStyle, (int) mBrushSize);
        int m = (int) mBrushSize;
        float f = mPatternManager.getAlphaScale(mBrushStyle);

        if (mBrushFlow > 210) {
            mAdjustedAlpha = mBrushFlow;
        } else {
            if (k > m) {
                mAdjustedAlpha = mBrushFlow;
            } else {
                mAdjustedAlpha = (int) (mBrushFlow / f);
            }
        }
//        Log.e("TAG", "Pattern Manager Logs max " + mBrushMaxSize + " currentSize " + mBrushSize);
        mBrushPaint.setAlpha(mAdjustedAlpha);
    }

    protected int randomColor() {
        if (mRandomColorPicker != null)
            return mRandomColorPicker.getRandomColor();
        return -65536;
    }

    public void release() {
        super.release();
        mRandom = null;
        mBound = null;
        sp = null;
        cp = null;
        ep = null;
    }

    public void restoreBrush(float[] pArrayOfFloat) {
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void setAlpha(int pInt) {
        mBrushAlphaValue = pInt;
    }

    public void setPatternManager(PatternManager pPatternManager) {
        mPatternManager = pPatternManager;
        /*mBrushMaxSize = pPatternManager.getMaxSize(mBrushStyle);
        mBrushMinSize = pPatternManager.getMinSize(mBrushStyle);*/
        mBrushMaxSize = pPatternManager.getMaxSize(mBrushStyle);
        mBrushMinSize = pPatternManager.getMinSize(mBrushStyle);

    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    @Override
    public void updateBrush() {
        // TODO Auto-generated method stub

    }
}
