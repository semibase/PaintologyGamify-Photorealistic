package com.paintology.lite.photorealistic.drawing.painting;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.brush.BrushMetaData;

import java.util.ArrayList;

public class Stroker {
    private String TAG = "Stroker";
    private Rect emptyRect;
    private Brush mBrush;
    public BrushMetaData mBrushMetaData;
    public Rect mDirtyRect;
    public ArrayList<Point> mPoints;

    public Stroker() {
        ArrayList lArrayList = new ArrayList();
        mPoints = lArrayList;
        Rect lRect1 = new Rect();
        mDirtyRect = lRect1;
        Rect lRect2 = new Rect();
        emptyRect = lRect2;
        mBrush = null;
    }

    public void composeStrokePath() {
    }

    public void finish() {
        if (mBrush != null) {
            mBrush.endStroke();
            mBrush = null;
        }
        mDirtyRect.setEmpty();
    }

    public Brush getBrush() {
        return mBrush;
    }

    public int getBrushStyle() {
        return mBrushMetaData.mBrushStyle;
    }

    public Rect lazyStrokeTo(Canvas pCanvas, Point pPoint) {
        float f1 = pPoint.x;
        float f2 = pPoint.y;
        try {
            mBrush.lazyStrokeTo(f1, f2);
            return emptyRect;
        } catch (NullPointerException lNullPointerException) {
            lNullPointerException.printStackTrace();
            mDirtyRect.setEmpty();

            return mDirtyRect;
        }
    }

    public void setBrush(Brush pBrush) {
        mBrush = pBrush;
    }

    public void start() {
        mDirtyRect.setEmpty();
    }

    public Rect strokeAllPoints(Canvas pCanvas, Brush pBrush) {
        if (pBrush.mBrushStyle >= 512)
            Log.e(TAG, "strokeAllPoints for pattern brush");
        Path lPath = new Path();
        Point lPoint1 = new Point();
        Point lPoint2 = new Point();
        Point lPoint3 = new Point();
        Point lPoint4 = (Point) mPoints.get(0);
        lPoint1.set(lPoint4);
        lPoint2.set(lPoint4);
        float f1 = lPoint4.x;
        float f2 = lPoint4.y;
        lPath.moveTo(f1, f2);
        int j = 1;
        while (true) {
            int k = mPoints.size() + -1;
            if (j >= k) {
                Point lPoint5 = (Point) mPoints.get(j);
                float f3 = lPoint2.x;
                float f4 = lPoint2.y;
                float f5 = lPoint5.x;
                float f6 = lPoint5.y;
                lPath.quadTo(f3, f4, f5, f6);
                float[] arrayOfFloat = lPoint5.data;
                pBrush.restoreBrush(arrayOfFloat);
                pBrush.preparePaint();
                return pBrush.drawStroke(pCanvas, lPath);
            }
            Point lPoint6 = (Point) mPoints.get(j);
            float f7 = lPoint2.x;
            float f8 = lPoint6.x;
            float f9 = (f7 + f8) / 2.0F;
            lPoint3.x = f9;
            float f10 = lPoint2.y;
            float f11 = lPoint6.y;
            float f12 = (f10 + f11) / 2.0F;
            lPoint3.y = f12;
            float f13 = lPoint2.x;
            float f14 = lPoint2.y;
            float f15 = lPoint3.x;
            float f16 = lPoint3.y;
            lPath.quadTo(f13, f14, f15, f16);
            lPoint1.set(lPoint3);
            lPoint2.set(lPoint6);
            j += 1;
        }
    }

    public Rect strokeEnd(Canvas pCanvas, Point pPoint, boolean pBoolean) {
        float f1 = pPoint.x;
        float f2 = pPoint.y;

        if (!pBoolean)
            mBrush.updateBrush();
        else {
            mBrush.restoreBrush(pPoint.data);
        }

        try {
            Rect lRect = mBrush.strokeEnd(pCanvas, f1, f2);

            if (lRect != null) {
                mDirtyRect.union(lRect);
                pPoint.data = mBrush.archiveBrush();
                mPoints.add(pPoint);

                return lRect;
            }

            return null;
        } catch (NullPointerException lNullPointerException) {
            lNullPointerException.printStackTrace();
            mDirtyRect.setEmpty();
            return mDirtyRect;
        }
    }

    public void strokeFrom(Point point, boolean pBoolean) {
        float f1 = point.x;
        float f2 = point.y;

        if (!pBoolean)
            mBrush.prepareBrush();
        else
            mBrush.preparePaint();

        mBrushMetaData = new BrushMetaData(mBrush);
        mBrush.strokeFrom(f1, f2);

        point.data = mBrush.archiveBrush();
        mPoints.add(point);
    }

    public Rect strokeTo(Canvas pCanvas, Point pPoint, boolean pBoolean) {
        float f1 = pPoint.x;
        float f2 = pPoint.y;

        if (!pBoolean)
            mBrush.updateBrush();
        else
            mBrush.restoreBrush(pPoint.data);
        try {
            Rect lRect = mBrush.strokeTo(pCanvas, f1, f2);

            if (lRect != null) {
                mDirtyRect.union(lRect);
                pPoint.data = mBrush.archiveBrush();
                mPoints.add(pPoint);

                return lRect;
            }

            return null;
        } catch (NullPointerException lNullPointerException) {
            lNullPointerException.printStackTrace();
            mDirtyRect.setEmpty();

            return mDirtyRect;
        }
    }
}
