package com.paintology.lite.photorealistic.drawing.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.bezier.QuadCurve;
import com.paintology.lite.photorealistic.drawing.colorpicker.RandomColorPicker;
import com.paintology.lite.photorealistic.drawing.painting.Point;
import com.paintology.lite.photorealistic.drawing.painting.Stroker;
import com.paintology.lite.photorealistic.drawing.pattern.PatternManager;

import java.util.ArrayList;
import java.util.Random;

public abstract class Brush {


//    public static final int CharcoalBrush = 576;


    public static final int EmbossBrush = 96;
    public static final int EraserBrush = 112;

    /*public static final int ColorWaxBrush = 592;
    public static final int DryOilBrush = 528;
    public static final int FlahWetWaterColorBrush = 608;
    public static final int FlatYingMaoBrush = 656;
    public static final int HalfDryOilBrush = 544;
    public static final int LaBiBrush = 784;
    public static final int OilWaxBrush = 560;
    public static final int PatternBrush = 512;
*/
    public static final int LineBrush = 81;
    public static final int RainboBrush = 39;


    public static final int felt = 45;
    public static final int halo = 46;
    public static final int outline = 47;
    public static final int dash_line = 48;
    public static final int PenInkBrush = 41;
    //    public static final int descret_line = 49;
//    public static final int composite_line = 50;
//    public static final int leaf_line = 51;
//    public static final int oval_line = 52;
//    public static final int arrow_line = 53;
//    public static final int shadow_line = 57;

    public static final int cube_line = 54;
    public static final int InkPenBrushNew = 56;
/*
    public static final int lane = 559;
    public static final int fountain = 561;
    public static final int streak = 562;
    public static final int foliage = 563;

    public static final int RoundYingMaoBrush = 640;
    public static final int RoundYingMaoRoughBrush = 641;
    public static final int RoundYingMaoSmoothBrush = 642;
*/

    public static final int SketchBrush = 64;
    public static final int SketchFurBrush = 257;

    public static final int SketchSingleLineBrush = 265;

    public static final int SketchyBrush = 256;

    public static final int SketchyLineBrush = 272;

    public static final int SprayBrush = 80;

    /*public static final int StarSprayBrush = 624;
    public static final int TanSuBrush = 768;
    */
    public static final int WatercolorBrush = 55;
    private String TAG = "Brush";
    RectF bound;
    RectF boundRectF;
    public int mBrushAlphaValue = 255;
    public int mBrushArchiveDataSize;
    public int mBrushColor = -1;
    public int mBrushDirection;
    public int mBrushFlow;
    public int mBrushGreyValue;
    public boolean mBrushHasAlpha;
    public int mBrushMaxAlpha = 255;
    public float mBrushMaxSize;
    public float mBrushMinSize;
    public int mBrushMode;
    public float mBrushOriginalSize;
    protected Paint mBrushPaint;
    public Bitmap mBrushPattern;
    public int mBrushPatternHeight;
    public int mBrushPatternWidth;
    public Bitmap mBrushRawPattern;
    public int mBrushRawPatternHeight;
    public int mBrushRawPatternWidth;
    public float mBrushSize;
    public int mBrushStyle;
    public String mBrushStyleName;
    protected Point mCtrlPoint;
    public Rect mDirtyRect;
    protected Point mEndPoint;
    public boolean mHasGlobalAlpha = false;
    public int mHistoryStrokesToConnect;
    public boolean mIsAnimationBrush;
    public boolean mIsRandomColor = false;
    public boolean mMustRedrawWholeStrokePath;
    public ArrayList<Stroker> mPaintingStrokeList;
    public PatternManager mPatternManager;
    public QuadCurve mQuadCurve;
    public Random mRandom;
    protected RandomColorPicker mRandomColorPicker;
    public long mRandomSeed;
    public int mRepeatDrawTimes;
    protected float mSizeBias = 1.0F;
    public float mSizeLowerBound;
    public float mSizeUpperBound;
    protected Point mStartPoint;
    protected Path mStrokePath;
    public boolean mSupportFlow = false;
    public boolean mSupportOpacity = true;

    public Brush() {
        mDirtyRect = new Rect();
        mBrushPaint = new Paint();
        mBrushPaint.setAntiAlias(true);
        mRandom = new Random();

        mHistoryStrokesToConnect = 0;
        mMustRedrawWholeStrokePath = false;
        mIsAnimationBrush = false;
        mBrushMode = 33;
        mBrushArchiveDataSize = 0;
        mStartPoint = new Point();
        mCtrlPoint = new Point();
        mEndPoint = new Point();
        mStrokePath = new Path();
        mRepeatDrawTimes = 1;
        boundRectF = new RectF();
        bound = new RectF();
    }

    /*public Brush(int pInt) {
        mDirtyRect = new Rect();
        mBrushPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBrushPaint.setAntiAlias(true);
        mRandom = new Random();
        mHistoryStrokesToConnect = 0;
        mMustRedrawWholeStrokePath = false;
        mIsAnimationBrush = false;
        mBrushMode = 33;
        mBrushArchiveDataSize = 0;
        mStartPoint = new Point();
        mCtrlPoint = new Point();
        mEndPoint = new Point();
        mStrokePath = new Path();
        mRepeatDrawTimes = 1;
        boundRectF = new RectF();
        bound = new RectF();
        mBrushMode = pInt;
    }

    public Brush(Brush pBrush) {
        mDirtyRect = new Rect();
        mBrushPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBrushPaint.setAntiAlias(true);
        mRandom = new Random();
        mHistoryStrokesToConnect = 0;
        mMustRedrawWholeStrokePath = false;
        mIsAnimationBrush = false;
        mBrushMode = 33;
        mBrushArchiveDataSize = 0;
        mStartPoint = new Point();
        mCtrlPoint = new Point();
        mEndPoint = new Point();
        mStrokePath = new Path();
        mRepeatDrawTimes = 1;
        boundRectF = new RectF();
        bound = new RectF();

        cloneBrush(pBrush);
    }*/

    public static Brush createBrush(int pInt) {
        Brush lObject = null;
        Log.e("TAG", "Selected Brush Type " + pInt);
        if (pInt == PenInkBrush) {
            lObject = new PenInkBrush();
        } else if (pInt == SketchBrush) {
            lObject = new SketchBrush(17);
        } else if (pInt == SketchSingleLineBrush) {
            lObject = new SketchSingleLineBrush();
        } else if (pInt == SketchyLineBrush) {
            lObject = new SketchyLineBrush();
        } else if (pInt == WatercolorBrush) {
            lObject = new WatercolorBrush(33);
        } else if (pInt == SprayBrush) {
            lObject = new SprayBrush();
        } else if (pInt == LineBrush) {
            lObject = new LineBrush();
        } else if (pInt == EmbossBrush) {
            lObject = new EmbossBrush();
        } else if (pInt == EraserBrush) {
            lObject = new EraserBrush();
        } else if (pInt == SketchyBrush) {
            lObject = new SketchyBrush();
        } else if (pInt == SketchFurBrush) {
            lObject = new SketchFurBrush();
        } else if (pInt == RainboBrush) {
            lObject = new RainbowBrush();
        } else if (pInt == InkPenBrushNew) {
            lObject = new InkPenBrushNew();
        } else if (pInt == felt) {
            lObject = new Normal_1();
        } else if (pInt == halo) {
            lObject = new SprayBrushSolid();
        } else if (pInt == outline) {
            lObject = new SprayBrushOuter();
        } else if (pInt == dash_line) {
            lObject = new DashPathLine();
        } else if (pInt == cube_line) {
            lObject = new CubeLineBrush();
        } else {
            lObject = new PatternBrush(pInt, null);
        }

        if (lObject == null)
            return new LineBrush();
        return lObject;
    }

    /*public static Brush createBrush(int pInt, PatternManager pPatternManager) {
        return new PatternBrush(pInt, pPatternManager);
    }*/

    private void setRGB(Bitmap pBitmap, int pInt) {
        int i;
        if (pBitmap != null) {
            for (i = 0; i < pBitmap.getHeight(); i++) {
                for (int k = 0; k < pBitmap.getWidth(); k++) {
                    int n = pBitmap.getPixel(k, i);
                    int i1 = 0xFFFFFF & pInt;
                    int i2 = 0xFF000000 & n;
                    int i3 = i1 | i2;
                    pBitmap.setPixel(k, i, i3);
                }
            }
        }
    }

    public abstract float[] archiveBrush();

    public abstract int[] archivePaint();


    protected void clampBrushSize() {
        mBrushSize = Math.min(mBrushMaxSize, mBrushSize);
        mBrushSize = Math.max(mBrushMinSize, mBrushSize);
    }

    public void cloneBrush(Brush pBrush) {
        mBrushRawPatternWidth = pBrush.mBrushRawPatternWidth;
        mBrushRawPatternHeight = pBrush.mBrushRawPatternHeight;
        mBrushStyle = pBrush.mBrushStyle;
        mBrushColor = pBrush.mBrushColor;
        mBrushHasAlpha = pBrush.mBrushHasAlpha;
        mBrushAlphaValue = pBrush.mBrushAlphaValue;
        mBrushDirection = pBrush.mBrushDirection;
        mBrushGreyValue = pBrush.mBrushGreyValue;
        mBrushMaxSize = pBrush.mBrushMaxSize;
        mBrushMinSize = pBrush.mBrushMinSize;
        mBrushOriginalSize = pBrush.mBrushOriginalSize;
        mBrushSize = pBrush.mBrushSize;
        mBrushPattern = pBrush.mBrushPattern;
        mBrushPatternWidth = pBrush.mBrushPatternWidth;
        mBrushPatternHeight = pBrush.mBrushPatternHeight;
    }

    public abstract void draw(Canvas pCanvas, Path pPath);

    public abstract void draw(Canvas pCanvas, Path pPath, int pInt);

    public abstract void draw(Canvas pCanvas, Point[] pArrayOfPoint);

    public abstract void draw(Canvas pCanvas, Point[] pArrayOfPoint, int pInt);

    public void drawLine(Canvas pCanvas, Point pPoint1, Point pPoint2) {
        Log.e(TAG, "drawLine called");
        int i = (int) pPoint1.x;
        int j = (int) pPoint1.y;
        Random lRandom = new Random();
        for (int k = 0; k < 100; k++) {
            i += lRandom.nextInt(2);
            j += lRandom.nextInt(2);

            if (mBrushPattern != null && !mBrushPattern.isRecycled())
                pCanvas.drawBitmap(mBrushPattern, i, j, null);
        }
    }

    public Rect drawStroke(Canvas pCanvas, Path pPath) {
        pCanvas.drawPath(pPath, mBrushPaint);
        updateDirtyRect(pPath);
        return mDirtyRect;
    }

    public Rect drawStroke(Canvas pCanvas, Point pPoint1, Point pPoint2, Point pPoint3) {
        return null;
    }

    protected Rect drawSubStroke(Canvas pCanvas) {
        Point lPoint1;
        Point lPoint2;
        Point lPoint3;

        getPaint().setAntiAlias(true);

        if (!mMustRedrawWholeStrokePath) {
            mStrokePath.reset();
            mStrokePath.moveTo(mStartPoint.x, mStartPoint.y);
            mStrokePath.quadTo(mCtrlPoint.x, mCtrlPoint.y, mEndPoint.x, mEndPoint.y);
        } else {
            mStrokePath.quadTo(mCtrlPoint.x, mCtrlPoint.y, mEndPoint.x, mEndPoint.y);
        }

        Rect lRect = null;

        if (mBrushStyle < 512) {
            lRect = drawStroke(pCanvas, mStrokePath);
        } else {
            lPoint1 = mStartPoint;
            lPoint2 = mCtrlPoint;
            lPoint3 = mEndPoint;
//            Log.e("TAG", "drawStroke called 402");
            lRect = drawStroke(pCanvas, lPoint1, lPoint2, lPoint3);
        }
        return lRect;
    }

    public abstract void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2);

    public void endStroke() {
    }

    public int getAlpha() {
        return mBrushAlphaValue;
    }


    public int getColor() {
        return mBrushColor;
    }


    public Paint getPaint() {
        return mBrushPaint;
    }

    public RandomColorPicker getRandomColorPicker() {
        return mRandomColorPicker;
    }

    public float getSize() {
        return mBrushSize;
    }

    public void lazyStrokeTo(float pFloat1, float pFloat2) {
        mEndPoint.x = (mCtrlPoint.x + pFloat1) / 2.0F;
        mEndPoint.y = (mCtrlPoint.y + pFloat2) / 2.0F;
        mStrokePath.quadTo(mCtrlPoint.x, mCtrlPoint.y, mEndPoint.x, mEndPoint.y);
        mStartPoint.set(mEndPoint);
        mCtrlPoint.set(pFloat1, pFloat2);
    }

    public void prepareBrush() {
    }

    public void preparePaint() {
    }

    public void release() {
        mBrushRawPattern = null;
        mBrushPattern = null;
        mDirtyRect = null;
        mBrushPaint = null;
        mRandomColorPicker = null;
        mPatternManager = null;
        mQuadCurve = null;
        mPaintingStrokeList = null;
    }


    public void resizePattern() {
    }

    public abstract void restoreBrush(float[] pArrayOfFloat);

    public void restoreBrushMetaData(BrushMetaData pBrushMetaData) {
        mBrushStyle = pBrushMetaData.mBrushStyle;
        mBrushSize = pBrushMetaData.mBrushSize;
        mBrushColor = pBrushMetaData.mBrushColor;
        mBrushAlphaValue = pBrushMetaData.mBrushAlphaValue;
        mBrushDirection = pBrushMetaData.mBrushDirection;
        mBrushFlow = pBrushMetaData.mBrushFlow;
        mRandomSeed = pBrushMetaData.mRandomSeed;
        mRepeatDrawTimes = pBrushMetaData.mRepeatDrawTimes;
    }

    public abstract void restorePaint(int[] pArrayOfInt);

    public void restorePaint() {

    }


    public void setAlpha(int pInt) {
        mBrushAlphaValue = pInt;
    }

    public void setColor(int pInt) {
        mBrushColor = pInt;

        if (mBrushPattern != null) {
            setRGB(mBrushPattern, pInt);
        }
    }

    public void setMode(int pInt) {
        mBrushMode = pInt;
        if ((mBrushMode != 33) && (mBrushMode != 17))
            Log.e(TAG, "Invalid kid/artist mode");

        if (mBrushMode == 17)
            mIsRandomColor = true;
        else
            mIsRandomColor = false;
    }

    public void setPatternManager(PatternManager pPatternManager) {
        mPatternManager = pPatternManager;
    }

    public void setQuadDecompositor(QuadCurve pQuadCurve) {
        mQuadCurve = pQuadCurve;
    }

    public void setRandomColorPicker(RandomColorPicker pRandomColorPicker) {
        mRandomColorPicker = pRandomColorPicker;
    }

    public void setScale(float pFloat) {
    }

    public void setSize(float pFloat) {
        mBrushSize = pFloat;
        clampBrushSize();
        setSizeBound();
        resizePattern();
    }

    protected void setSizeBound() {
        mSizeLowerBound = Math.max(mBrushSize - mSizeBias, mBrushMinSize);
        mSizeUpperBound = Math.min(mBrushSize + mSizeBias, mBrushMaxSize);
    }

    public abstract void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2);

    public void startStroke() {
    }

    public Rect strokeEnd(Canvas pCanvas, float pFloat1, float pFloat2) {
        mEndPoint.x = pFloat1;
        mEndPoint.y = pFloat2;

        return drawSubStroke(pCanvas);
    }

    public void strokeFrom(float pFloat1, float pFloat2) {
        mStartPoint.set(pFloat1, pFloat2);
        mCtrlPoint.set(pFloat1, pFloat2);
        mStrokePath.reset();
        mStrokePath.moveTo(pFloat1, pFloat2);
    }


    public Rect strokeTo(Canvas pCanvas, float pFloat1, float pFloat2) {
        mEndPoint.x = (mCtrlPoint.x + pFloat1) / 2.0F;
        mEndPoint.y = (mCtrlPoint.y + pFloat2) / 2.0F;

        Rect rc = drawSubStroke(pCanvas);

        mStartPoint.set(mEndPoint);
        mCtrlPoint.set(pFloat1, pFloat2);

        return rc;
    }

    protected void unionDirtyRect(Path pPath) {
        pPath.computeBounds(bound, false);
        int i = (int) Math.max((int) bound.left - mBrushPaint.getStrokeWidth(), 0);
        int j = (int) ((int) bound.right + mBrushPaint.getStrokeWidth());
        int k = (int) Math.max((int) bound.top - mBrushPaint.getStrokeWidth(), 0);
        int m = (int) ((int) bound.bottom + mBrushPaint.getStrokeWidth());
        mDirtyRect.union(i, k, j, m);
    }

    public abstract void updateBrush();

    protected void updateDirtyRect(Path pPath) {
        pPath.computeBounds(boundRectF, false);
        float f1 = boundRectF.left;
        float f2 = mBrushPaint.getStrokeWidth();
        float f3 = f1 - f2;
        float f4 = boundRectF.right;
        float f5 = mBrushPaint.getStrokeWidth();
        float f6 = f4 + f5;
        float f7 = boundRectF.top;
        float f8 = mBrushPaint.getStrokeWidth();
        float f9 = f7 - f8;
        float f10 = boundRectF.bottom;
        float f11 = mBrushPaint.getStrokeWidth();
        float f12 = f10 + f11;
        int i = Math.max((int) f3, 0);
        int j = (int) f6;
        int k = Math.max((int) f9, 0);
        int m = (int) f12;
        mDirtyRect.set(i, k, j, m);
    }

    protected void updateDirtyRect(Path pPath, float pFloat) {
        pPath.computeBounds(boundRectF, false);
        float f1 = boundRectF.left - pFloat;
        float f2 = boundRectF.right + pFloat;
        float f3 = boundRectF.top - pFloat;
        float f4 = boundRectF.bottom + pFloat;
        int i = Math.max((int) f1, 0);
        int j = (int) f2;
        int k = Math.max((int) f3, 0);
        int m = (int) f4;
        mDirtyRect.set(i, k, j, m);
    }

    protected void updateDirtyRect(Point pPoint1, Point pPoint2) {
        float f1 = pPoint1.x;
        float f2 = pPoint2.x;
        float f3 = Math.min(f1, f2);
        float f4 = mBrushPaint.getStrokeWidth();
        float f5 = f3 - f4;
        float f6 = pPoint1.x;
        float f7 = pPoint2.x;
        float f8 = Math.max(f6, f7);
        float f9 = mBrushPaint.getStrokeWidth();
        float f10 = f8 + f9;
        float f11 = pPoint1.y;
        float f12 = pPoint2.y;
        float f13 = Math.min(f11, f12);
        float f14 = mBrushPaint.getStrokeWidth();
        float f15 = f13 - f14;
        float f16 = pPoint1.y;
        float f17 = pPoint2.y;
        float f18 = Math.max(f16, f17);
        float f19 = mBrushPaint.getStrokeWidth();
        float f20 = f18 + f19;
        int i = Math.max((int) f5, 0);
        int j = (int) f10;
        int k = Math.max((int) f15, 0);
        int m = (int) f20;
        mDirtyRect.set(i, k, j, m);
    }
}	
