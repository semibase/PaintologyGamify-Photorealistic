package com.paintology.lite.photorealistic.drawing.bezier;

import android.graphics.PointF;

public class QuadCurve {
    final int BEZIER_MAX_SPLINE_DECOMPOSE = 100;

    int mNumPoints;
    PointF[] mPolygonPoints = new PointF[500];
    private String TAG;
    private float THREADHOLD_DISTANCE;
    Bezier[] mBezierBuf;

    public QuadCurve() {
        Bezier[] arrayOfBezier1 = new Bezier[100];
        mBezierBuf = arrayOfBezier1;
        THREADHOLD_DISTANCE = 2.5F;
        TAG = "QuadCruve";
        int i = 0;
        int j = mPolygonPoints.length;

        for (i = 0; i < mPolygonPoints.length; i++)
            mPolygonPoints[i] = new PointF();

        for (i = 0; i < 100; i++)
            mBezierBuf[i] = new Bezier();
    }

    private void BezierSplit(Bezier pBezier1, Bezier pBezier2, Bezier pBezier3) {
        pBezier2.point[0].x = pBezier1.point[0].x;
        pBezier2.point[1].x = (pBezier1.point[0].x + pBezier1.point[1].x) / 2.0F;
        pBezier2.point[2].x = (pBezier1.point[0].x + pBezier1.point[1].x) / 2.0F;
        pBezier3.point[1].x = (pBezier1.point[1].x + pBezier1.point[3].x) / 2.0F;
        pBezier3.point[2].x = (pBezier1.point[1].x + pBezier1.point[3].x) / 2.0F;
        pBezier3.point[3].x = pBezier1.point[3].x;

        pBezier3.point[0].x = (((pBezier1.point[0].x + pBezier1.point[1].x) / 2.0F) +
                ((pBezier1.point[1].x + pBezier1.point[3].x) / 2.0F)) / 2.0F;
        pBezier2.point[3].x = (((pBezier1.point[0].x + pBezier1.point[1].x) / 2.0F) +
                ((pBezier1.point[1].x + pBezier1.point[3].x) / 2.0F)) / 2.0F;

        pBezier2.point[0].y = pBezier1.point[0].y;
        pBezier2.point[1].y = (pBezier1.point[0].y + pBezier1.point[1].y) / 2.0F;
        pBezier2.point[2].y = (pBezier1.point[0].y + pBezier1.point[1].y) / 2.0F;
        pBezier3.point[1].y = (pBezier1.point[1].y + pBezier1.point[3].y) / 2.0F;
        pBezier3.point[2].y = (pBezier1.point[1].y + pBezier1.point[3].y) / 2.0F;
        pBezier3.point[3].y = pBezier1.point[3].y;

        pBezier3.point[0].y = (((pBezier1.point[0].y + pBezier1.point[1].y) / 2.0F) +
                ((pBezier1.point[1].y + pBezier1.point[3].y) / 2.0F)) / 2.0F;
        pBezier2.point[3].y = (((pBezier1.point[0].y + pBezier1.point[1].y) / 2.0F) +
                ((pBezier1.point[1].y + pBezier1.point[3].y) / 2.0F)) / 2.0F;
    }


    private void DumpPoint(String pString, PointF pPointF) {
    }

    private void addPoint(PointF pPointF) {
        if (mNumPoints >= 500)
            return;

        if (mNumPoints > 0) {
            if (isIdentical(mPolygonPoints[mNumPoints], pPointF)) {
                return;
            }
        }

        if (mNumPoints < 500) {
            mPolygonPoints[mNumPoints].set(pPointF);
            mNumPoints += 1;
        }
    }

    private float distance(PointF pPointF1, PointF pPointF2) {
        float f1 = pPointF1.x;
        float f2 = pPointF2.x;
        float f3 = Math.abs(f1 - f2);
        float f4 = pPointF1.y;
        float f5 = pPointF2.y;
        float f6 = Math.abs(f4 - f5);

        return f3 + f6;
    }

    private boolean isIdentical(PointF pPointF1, PointF pPointF2) {
        float f1 = pPointF1.x;
        float f2 = pPointF2.x;

        if (Math.abs(f1 - f2) < 0.5D) {
            float f3 = pPointF1.y;
            float f4 = pPointF2.y;

            if (Math.abs(f3 - f4) < 0.5D)
                return true;
        }

        return false;
    }

    private boolean isShortEnough(PointF pPointF1, PointF pPointF2) {
        float f1 = distance(pPointF1, pPointF2);
        float f2 = THREADHOLD_DISTANCE;

        if (f1 < f2)
            return true;

        return false;
    }

    public void Decompose(PointF pPointF1, PointF pPointF2, PointF pPointF3) {
        int i = 0;
        mNumPoints = 0;
        mBezierBuf[0].point[0].set(pPointF1);
        mBezierBuf[0].point[1].set(pPointF2);
        mBezierBuf[0].point[2].set(pPointF2);
        mBezierBuf[0].point[3].set(pPointF3);
        DumpPoint("sp ", pPointF1);
        DumpPoint("cp ", pPointF1);
        DumpPoint("ep ", pPointF3);
        addPoint(pPointF1);

        for (int j = 0; j < mNumPoints - 1; j++) {
            if (isShortEnough(mPolygonPoints[j], mPolygonPoints[j + 1])) {

            }
        }

        while (i >= 0 && i < BEZIER_MAX_SPLINE_DECOMPOSE - 1) {
            if ((isShortEnough(mBezierBuf[i].point[0], mBezierBuf[i].point[1])) && (isShortEnough(mBezierBuf[i].point[2], mBezierBuf[i].point[3]))) {
                addPoint(mBezierBuf[i].point[2]);
                addPoint(mBezierBuf[i].point[3]);
//				addPoint(mBezierBuf[i].point[4]);
                i--;

                if (i < 0)
                    break;
            } else {
                BezierSplit(mBezierBuf[i], mBezierBuf[i + 1], mBezierBuf[i]);
                i++;
            }
        }
    }

    public void finish() {
        mBezierBuf = null;
    }

    public int getPointNum() {
        return mNumPoints;
    }

    public PointF[] getPoints() {
        return mPolygonPoints;
    }

    public void setThreashold(float pFloat) {
        THREADHOLD_DISTANCE = pFloat;
    }

    public class Bezier {
        PointF[] point;

        public Bezier() {
            point = new PointF[8];

            for (int i = 0; i < point.length; i++) {
                point[i] = new PointF();
            }
        }
    }
}
