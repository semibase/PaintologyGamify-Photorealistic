package com.paintology.lite.photorealistic.drawing.brush;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.painting.Stroker;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class SketchFurBrush extends SketchyBrush {
    private String TAG = "FurBrush";
    StringConstants _constant;


    public SketchFurBrush() {
        setupBrush(33);
        mHistoryStrokesToConnect = 15;
        _constant = new StringConstants();
    }

    protected void drawSideStrokes(Canvas pCanvas, float pFloat1, float pFloat2) {
        int i = mPaintingStrokeList.size() - 1;
        int j = mHistoryStrokesToConnect;
        int k = i - j;

        if (k < 0)
            k = 0;

        Stroker lStroker = null;

        for (int m = i; m >= k; m--) {
            lStroker = (Stroker) mPaintingStrokeList.get(m);

            if (lStroker.getBrushStyle() == 257)
                break;

            if (lStroker.getBrushStyle() == 256)
                break;

        }

        if (lStroker != null) {
            for (int i4 = 0; i4 < lStroker.mPoints.size(); i4++) {
                Point lPoint = (Point) lStroker.mPoints.get(i4);
                float f1 = lPoint.x - pFloat1;
                float f2 = lPoint.y - pFloat2;
                float f5 = (f1 * f1) + (f2 * f2);

                if (f5 < 2000.0F) {
                    float f6 = mRandom.nextFloat();
                    float f7 = f5 / 2000.0F;
//	        if (f6 > f7)
                    {
                        mStrokePath.reset();

                        mStrokePath.moveTo(pFloat1 + 0.5F * f1, pFloat2 + 0.5F * f2);
                        mStrokePath.lineTo(pFloat1 - 0.5F * f1, pFloat2 - 0.5F * f2);
                        pCanvas.drawPath(mStrokePath, mBrushPaint);
                        unionDirtyRect(mStrokePath);
                    }
                }
            }
        }
    }


    protected void setupBrush(int pInt) {
        super.setupBrush(pInt);
        mBrushStyle = 257;

        try {
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
        } catch (Exception e) {
            Log.e("TAG", "Exception at setup Brush " + e.toString());
        }
    }
}
