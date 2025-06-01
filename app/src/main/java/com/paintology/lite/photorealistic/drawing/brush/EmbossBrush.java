package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class EmbossBrush extends Brush {
    private String TAG = "EmbossBrush";
    private float mBlurRadius;
    private MaskFilter mEmbossFilter;
    StringConstants _constant = new StringConstants();

    public EmbossBrush() {
        mBrushStyle = 96;
        mBrushMode = 17;

        if (mBrushMode == 17)
            mIsRandomColor = true;
        else
            mIsRandomColor = false;

        /*mBrushMaxSize = 35.0F;
        mBrushMinSize = 6.0F;
        mBrushSize = 12.0F;
        */

        int screenWidth = _constant.getInt(_constant._scree_width, MyApplication.getInstance());
        int screenHeight = _constant.getInt(_constant._scree_height, MyApplication.getInstance());
        int orientation = MyApplication.getInstance().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            if (screenHeight <= 1024) {
                mBrushMaxSize = 100.0F;
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
                mBrushMaxSize = 100.0F;
                mBrushMinSize = 0.01F;
            } else if (screenWidth > 1024 && screenWidth <= 2000) {
                mBrushMaxSize = 225.0f;
                mBrushMinSize = 0.01f;
            } else {
                mBrushMaxSize = 325.0f;
                mBrushMinSize = 0.01f;
            }
        }


        Log.e("TAG", "Line Brush Sizes " + _constant.getInt(_constant._scree_width, MyApplication.getInstance()) + " " + _constant.getInt(_constant._scree_height, MyApplication.getInstance()) + " mBrushMaxSize " + mBrushMaxSize);

        mBrushSize = 12.0F;

        mMustRedrawWholeStrokePath = true;
        mSupportOpacity = false;
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

    public android.graphics.Rect drawStroke(Canvas pCanvas, Path pPath) {

        pCanvas.drawPath(pPath, mBrushPaint);
        updateDirtyRect(pPath, mBrushSize + mBlurRadius);

        return mDirtyRect;
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
    }

    public void prepareBrush() {
        if (mBrushMode == 17) {
            if (mRandomColorPicker == null)
                Log.e(TAG, "no random color picker");
            else
                mBrushColor = mRandomColorPicker.getRandomColor();
        }
        preparePaint();
    }

    public void preparePaint() {
        float f1 = mBrushSize / 3.0F;
        float f2 = Math.max(1.0F, f1);
        mBlurRadius = f2;
        float[] arrayOfFloat = {1065353216, 1065353216, 1065353216};
        float f3 = mBlurRadius;
        EmbossMaskFilter lEmbossMaskFilter = new EmbossMaskFilter(arrayOfFloat, 0.3F, 6.0F, f3);
        mEmbossFilter = lEmbossMaskFilter;
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
        float f4 = mBrushSize;
        lPaint5.setStrokeWidth(f4);
        Paint lPaint6 = mBrushPaint;
        MaskFilter lMaskFilter1 = mEmbossFilter;
        MaskFilter lMaskFilter2 = lPaint6.setMaskFilter(lMaskFilter1);

    }

    public void restoreBrush(float[] pArrayOfFloat) {
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

