package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class SprayBrush extends Brush {
    private MaskFilter mBlurFilter;
    StringConstants _constant = new StringConstants();

    public SprayBrush() {
        mBrushStyle = 80;
        mBrushMode = 17;

        if (mBrushMode == 17)
            mIsRandomColor = true;
        else
            mIsRandomColor = false;


        int screenWidth = _constant.getInt(_constant._scree_width, MyApplication.getInstance());
        int screenHeight = _constant.getInt(_constant._scree_height, MyApplication.getInstance());
        int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            if (screenHeight <= 1024) {
                mBrushMaxSize = 125.0F;
                mBrushMinSize = 0.01F;
//                mBrushMinSize = 0.10F; // 0.03% of 125.0f = 0.10
//                mBrushMinSize = 1.25F; // 1% of 125.0f = 1.25
            } else if (screenHeight > 1024 && screenHeight <= 2000) {
                mBrushMaxSize = 225.0f;
                mBrushMinSize = 0.01f;
//                mBrushMinSize = 0.10f;
//                mBrushMinSize = 2.25f; // 1% of 225.0f = 2.25
            } else {
                mBrushMaxSize = 325.0f;
                mBrushMinSize = 0.01f;
//                mBrushMinSize = 0.10f;
//                mBrushMinSize = 3.25f; // 1% of 325.0f = 3.25
            }
        } else {
            // code for landscape mode
            if (screenWidth <= 1024) {
                mBrushMaxSize = 125.0F;
                mBrushMinSize = 0.01F;
//                mBrushMinSize = 0.10F;
//                mBrushMinSize = 1.25F; // 1% of 125.0f = 1.25
            } else if (screenWidth > 1024 && screenWidth <= 2000) {
                mBrushMaxSize = 225.0f;
                mBrushMinSize = 0.01f;
//                mBrushMinSize = 0.10f;
//                mBrushMinSize = 2.25f; // 1% of 225.0f = 2.25
            } else {
                mBrushMaxSize = 325.0f;
                mBrushMinSize = 0.01f;
//                mBrushMinSize = 0.10f;
//                mBrushMinSize = 3.25f; // 1% of 325.0f = 3.25
            }
        }


        /*mBrushMaxSize = 60.0F;
        mBrushMinSize = 1.0F;*/
        mBrushSize = 20.0F;
        mMustRedrawWholeStrokePath = true;

        mSupportOpacity = false;
        mSupportFlow = true;

        mBrushHasAlpha = true;
        mHasGlobalAlpha = true;

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

    // ERROR //
    public android.graphics.Rect drawStroke(Canvas pCanvas, Path pPath) {
        pCanvas.drawPath(pPath, mBrushPaint);
        updateDirtyRect(pPath, mBrushSize);
        return mDirtyRect;
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void prepareBrush() {
        if ((mBrushMode == 17) && (mRandomColorPicker != null)) {
            int i = mRandomColorPicker.getRandomColor();
            mBrushColor = i;
        }

        int k = mPatternManager.getSpacing(mBrushStyle, (int) mBrushSize);
        int m = (int) mBrushSize;
        float f = mPatternManager.getAlphaScale(mBrushStyle);

        if (mBrushFlow > 210) {
            mBrushAlphaValue = mBrushFlow;
        } else {
            if (k > m) {
                mBrushAlphaValue = mBrushFlow;
            } else {
                mBrushAlphaValue = (int) (mBrushFlow / f);
            }
        }

        preparePaint();
    }

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);
        Paint lPaint1 = mBrushPaint;
        Paint.Style lStyle = Paint.Style.STROKE;
        lPaint1.setStyle(lStyle);
        Paint lPaint2 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.ROUND;
        lPaint2.setStrokeCap(lCap);
        Paint lPaint3 = mBrushPaint;
        Paint.Join lJoin = Paint.Join.ROUND;
        lPaint3.setStrokeJoin(lJoin);
        Paint lPaint4 = mBrushPaint;
        int i = mBrushColor;
        lPaint4.setColor(i);
        Paint lPaint5 = mBrushPaint;
        float f1 = mBrushSize / 6.0F;
        float f2 = Math.max(1.0F, f1);
        lPaint5.setStrokeWidth(f2);
        float f3 = mBrushSize / 2.0F;
        BlurMaskFilter.Blur lBlur = BlurMaskFilter.Blur.NORMAL;
        BlurMaskFilter lBlurMaskFilter = new BlurMaskFilter(f3, lBlur);
        mBlurFilter = lBlurMaskFilter;
        Paint lPaint6 = mBrushPaint;
        MaskFilter lMaskFilter1 = mBlurFilter;
        lPaint6.setMaskFilter(lMaskFilter1);

        int j = mBrushAlphaValue;
        lPaint6.setAlpha(j);
    }

    public void restoreBrush(float[] pArrayOfFloat) {
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
