package com.paintology.lite.photorealistic.drawing.painting;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import com.paintology.lite.photorealistic.drawing.bezier.QuadCurve;
import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.colorpicker.RandomColorPicker;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.pattern.PatternManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

//import com.flurry.android.FlurryAgent;

public class Painting implements Serializable {

    private int MAX_SPEED;
    private String TAG = "Painting";
    public Paint backgroundPaint;
    Rect dirtyRect;
    public boolean isCanvasCreated;
    private Bitmap mBackgroundBitmap;
    private int mBackgroundColor;
    private Bitmap mBitmap;
    public int mBrushAlpha;
    public int mBrushColor;
    public boolean mBrushDemoMode;
    public int mBrushFlow;
    public int mBrushKidOrArtistMode;
    public float mBrushSize;
    public int mBrushStyle;
    private ArrayList<CachedIntermediatePaint> mCachedPaintingList;
    public ArrayList<CachedUndoStroke> mCachedUndoStrokeList;
    public Canvas mCanvas;
    PorterDuffXfermode mClearMode;
    Paint mClearPaint;
    Path mClearPath;
    RectF mClearRectF;
    public Rect mClipRegion;
    public Bitmap mComposeBitmap;
    private Canvas mComposeCanvas;
    Paint mCompositionPaint;
    PorterDuffXfermode mCopyMode;
    private int mCurStrokeIndex;
    private Stroker mCurStroker;
    public Brush mCurrentBrush;
    public Stack<CachedUndoStroke> mDeletedStrokes;
    private Rect mDirtyRegion;
    private Bitmap mDrawingBitmap;
    public Canvas mDrawingCanvas;
    private int mDrawingCanvasOapcity;
    private boolean mDrawingCanvasVisible;
    private int mFastForwardSpeed;
    public ArrayList<Stroker> mHisotryStrokeListOfBaseSnapShot;
    private boolean mIsDirty;
    public int mMovieStatus;
    public int mPaintingHeight;
    public int mPaintingWidth;
    private Context mParent;
    public PatternManager mPatternManager;
    private QuadCurve mQuadCurve;
    Random mRandom;
    private RandomColorPicker mRandomBrightColorPicker;
    private RandomColorPicker mRandomDarkColorPicker;
    private Bitmap mReplayBitmap;
    private Canvas mReplayCanvas;
    private int mReplayPaintingSpeed;
    private Thread mReplayPaintingThread;
    public boolean mSaved;
    private boolean mShallStopReplay;
    PorterDuffXfermode mSrcOverMode;
    private Point mStartPoint;
    private Point mStopPoint;
    private ArrayList<Stroker> mStrokerList;
    private Bitmap mUndoBaselineBitmap;
    private Canvas mUndoBaselineCanvas;
    private float mXDensity;
    private float mYDensity;
    private boolean mbHasBackgroundBitmap;
    private int redoClicks;
    private int undoClicks;

    public Painting(Context pContext) {
        mStrokerList = new ArrayList();
        mCurStrokeIndex = -1;
        mBitmap = null;
        mCanvas = null;
        mBackgroundBitmap = null;
        mbHasBackgroundBitmap = false;
        mDrawingBitmap = null;
        mDrawingCanvas = null;
        mDrawingCanvasOapcity = 255;
        mDrawingCanvasVisible = false;
        mSrcOverMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        mCopyMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mCompositionPaint = new Paint();
        mComposeBitmap = null;
        mComposeCanvas = null;
        mReplayBitmap = null;
        mReplayCanvas = null;
        mUndoBaselineBitmap = null;
        mUndoBaselineCanvas = null;
        mDirtyRegion = new Rect();
        mSaved = true;
        mBrushStyle = Brush.SketchBrush;
        mBrushKidOrArtistMode = 17;
        mBrushColor = -65536;
        mBrushSize = 5.0F;
        mBrushAlpha = 255;
        mBrushFlow = 255;
        mBrushDemoMode = false;
        mStartPoint = new Point();
        mStopPoint = new Point();
        mMovieStatus = 4;
        isCanvasCreated = false;
        backgroundPaint = new Paint();
        dirtyRect = new Rect();
        mRandom = new Random();
        mClearPath = new Path();
        mClearPaint = new Paint();
        mClearRectF = new RectF();
        mFastForwardSpeed = 1;
        MAX_SPEED = 8;

        mCachedPaintingList = new ArrayList();
        mHisotryStrokeListOfBaseSnapShot = new ArrayList();
        mCachedUndoStrokeList = new ArrayList();
        mDeletedStrokes = new Stack();
        undoClicks = 0;
        redoClicks = 0;
        mBrushStyle = 16;
        mBackgroundColor = 0xFFFFFFFF;
        mRandomBrightColorPicker = new RandomColorPicker(32,
                RandomColorPicker.ColorPref.BRIGHT_COLOR);
        mRandomDarkColorPicker = new RandomColorPicker(32,
                RandomColorPicker.ColorPref.DARK_COLOR);
        mQuadCurve = new QuadCurve();
        mParent = pContext;
        mPatternManager = new PatternManager(mParent);
    }

   /* private void cacheUndoStroke(Stroker pStroker, int pInt) {
        try {
            try {
                CachedUndoStroke lCachedUndoStroke1 = new CachedUndoStroke();

                lCachedUndoStroke1.strokeIndex = pInt;
                lCachedUndoStroke1.stroke = pStroker;
                lCachedUndoStroke1.strokeRegion = new
                        Rect(pStroker.mDirtyRect);
                lCachedUndoStroke1.strokeRegion.intersect(mClipRegion);
                lCachedUndoStroke1.baseSnapShot = mUndoBaselineBitmap;

                if (mCachedUndoStrokeList.size() == 0) {
                    lCachedUndoStroke1.restoreStrokeList = new ArrayList();
                    lCachedUndoStroke1.restoreStrokeList.add(pStroker);
                } else {
                    CachedUndoStroke lCachedUndoStroke2;

                    lCachedUndoStroke2 =
                            (CachedUndoStroke) mCachedUndoStrokeList.get(mCachedUndoStrokeList.size() - 1);
                    lCachedUndoStroke1.restoreStrokeList = new ArrayList();

                    for (int j = 0; j < lCachedUndoStroke2.restoreStrokeList.size(); j++) {
                        Stroker lStroker = (Stroker) lCachedUndoStroke2.restoreStrokeList.get(j);
                        lCachedUndoStroke1.restoreStrokeList.add(lStroker);
                    }

                    lCachedUndoStroke1.restoreStrokeList.add(pStroker);
                }

                mCachedUndoStrokeList.add(lCachedUndoStroke1);
                undoClicks = 0;

                if (mCachedUndoStrokeList.size() > 10)
                    shrinkUndoCache();
//
            } catch (OutOfMemoryError lOutOfMemoryError) {
            }
        } catch (NullPointerException lNullPointerException) {

        } catch (IllegalArgumentException lIllegalArgumentException) {

        }
    }
*/

    // changes by shehroz solve undo limit
    private void cacheUndoStroke(Stroker pStroker, int pInt) {
        try {
            CachedUndoStroke lCachedUndoStroke1 = new CachedUndoStroke();
            lCachedUndoStroke1.strokeIndex = pInt;
            lCachedUndoStroke1.stroke = pStroker;
            lCachedUndoStroke1.strokeRegion = new Rect(pStroker.mDirtyRect);
            lCachedUndoStroke1.strokeRegion.intersect(mClipRegion);
            lCachedUndoStroke1.baseSnapShot = mUndoBaselineBitmap;

            if (mCachedUndoStrokeList.size() == 0) {
                lCachedUndoStroke1.restoreStrokeList = new ArrayList<>();
                lCachedUndoStroke1.restoreStrokeList.add(pStroker);
            } else {
                CachedUndoStroke lCachedUndoStroke2 = mCachedUndoStrokeList.get(mCachedUndoStrokeList.size() - 1);
                lCachedUndoStroke1.restoreStrokeList = new ArrayList<>(lCachedUndoStroke2.restoreStrokeList);
                lCachedUndoStroke1.restoreStrokeList.add(pStroker);
            }

            mCachedUndoStrokeList.add(lCachedUndoStroke1);
            undoClicks = 0;

            // Optional: You can remove the oldest undo stroke if you want to limit the cache size.
            // if (mCachedUndoStrokeList.size() > MAX_UNDO_STROKES)
            //     mCachedUndoStrokeList.remove(0);
        } catch (OutOfMemoryError lOutOfMemoryError) {
            // Handle OutOfMemoryError
        } catch (NullPointerException lNullPointerException) {
            // Handle NullPointerException
        } catch (IllegalArgumentException lIllegalArgumentException) {
            // Handle IllegalArgumentException
        }
    }


    private void clearCanvas(Canvas pCanvas, Rect pRect) {
        try {
            mClearPaint.setColor(16777215);
            mClearPaint.setStyle(Paint.Style.FILL);
            mClearPaint.setXfermode(mClearMode);

            mClearRectF.set(pRect);
            mClearPath.reset();
            mClearPath.addRect(mClearRectF, Path.Direction.CW);
            pCanvas.drawPath(mClearPath, mClearPaint);
        } catch (Exception lNullPointerException) {
        }
    }

    private void clearUndoStack() {
        for (int i = 0; i < mCachedUndoStrokeList.size(); i++) {
            ((CachedUndoStroke) mCachedUndoStrokeList.get(i)).destroy();
        }
        mCachedUndoStrokeList.clear();
    }

    private void clipDirtyRegion() {
        mDirtyRegion.intersect(mClipRegion);
    }

    private ArrayList<Stroker> cloneSnapshotHistoryStrokers() {
        ArrayList lArrayList = new ArrayList();

        for (int i = 0; i < mHisotryStrokeListOfBaseSnapShot.size(); i++) {
            lArrayList.add((Stroker) mHisotryStrokeListOfBaseSnapShot
                    .get(i));
        }

        return lArrayList;
    }

    private void composeDrawingCanvasToTempCanvas(Bitmap pBitmap,
                                                  Rect pRect) {
        if (!pRect.isEmpty()) {
            mCompositionPaint.setAlpha(mDrawingCanvasOapcity);
            mCompositionPaint.setXfermode(mSrcOverMode);
            clearCanvas(mComposeCanvas, pRect);
            mComposeCanvas.save();
            mComposeCanvas.clipRect(pRect);
            if (pBitmap != null && !pBitmap.isRecycled())
                mComposeCanvas.drawBitmap(pBitmap, 0.0F, 0.0F, null);

            if (mDrawingBitmap != null && !mDrawingBitmap.isRecycled())
                mComposeCanvas.drawBitmap(mDrawingBitmap, 0.0F, 0.0F,
                        mCompositionPaint);
            mComposeCanvas.restore();
        }
    }


    private void composeWholeStroke(Canvas pCanvas, Stroker pStroker) {
        if (!pStroker.mDirtyRect.isEmpty()) {
            Brush lBrush = pStroker.getBrush();

            if ((lBrush == null) || (!lBrush.mHasGlobalAlpha))
                mCompositionPaint.setAlpha(255);
            else
                mCompositionPaint.setAlpha(lBrush.mBrushAlphaValue);
        }

        mCompositionPaint.setXfermode(mSrcOverMode);
        pCanvas.save();
        pCanvas.clipRect(pStroker.mDirtyRect);
        if (mDrawingBitmap != null && !mDrawingBitmap.isRecycled())
            pCanvas.drawBitmap(mDrawingBitmap, 0.0F, 0.0F,
                    mCompositionPaint);
        pCanvas.restore();
    }

    private void dePrepareDrawingLayers() {
        mDrawingCanvasVisible = false;
        mDrawingCanvasOapcity = 255;
    }

    private void drawBackground(Canvas pCanvas) {

        if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
            pCanvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
        } else {
            backgroundPaint.setColor(mBackgroundColor);
            pCanvas.drawRect(mClipRegion, backgroundPaint);
        }
    }

    private void prepareDrawingLayers(Brush pBrush) {
        if (pBrush.mBrushStyle == 112)
            mDrawingCanvasVisible = false;
        else
            mDrawingCanvasVisible = true;

        if ((pBrush == null) || (!pBrush.mHasGlobalAlpha)) {
            mDrawingCanvasOapcity = 255;
        } else {
            mDrawingCanvasOapcity = pBrush.mBrushAlphaValue;
        }
    }

    private void showForwardSpeed() {
    }

    private void shrinkUndoCache() {

        CachedUndoStroke lCachedUndoStroke = (CachedUndoStroke) mCachedUndoStrokeList
                .get(0);
        undoRedoDrawStrokeOnCanvas(mUndoBaselineCanvas,
                lCachedUndoStroke.stroke, cloneSnapshotHistoryStrokers());
        mHisotryStrokeListOfBaseSnapShot.add(lCachedUndoStroke.stroke);

        if (mHisotryStrokeListOfBaseSnapShot.size() > 25)
            mHisotryStrokeListOfBaseSnapShot.remove(0);

        for (int i = 1; i < mCachedUndoStrokeList.size(); i++) {
            ((CachedUndoStroke) mCachedUndoStrokeList.get(i)).restoreStrokeList
                    .remove(0);
        }
        ((CachedUndoStroke) mCachedUndoStrokeList.get(0)).destroy();
        mCachedUndoStrokeList.remove(0);
    }

    private Rect undoRedoDrawStrokeOnCanvas(Canvas pCanvas,
                                            Stroker pStroker, ArrayList<Stroker> pArrayList) {
        Rect lRect1 = new Rect();
        Brush lBrush = Brush.createBrush(pStroker.getBrushStyle());
        lBrush.setPatternManager(mPatternManager);
        lBrush.setQuadDecompositor(mQuadCurve);
        lBrush.restoreBrushMetaData(pStroker.mBrushMetaData);
        lBrush.mPaintingStrokeList = pArrayList;

        if ((pStroker.getBrushStyle() == 53) || (pStroker.getBrushStyle() == 96) ||
                (pStroker.getBrushStyle() == 51))
            return pStroker.strokeAllPoints(pCanvas, lBrush);

        Stroker lStroker;

        lStroker = new Stroker();
        lStroker.setBrush(lBrush);
        lBrush.mPaintingStrokeList.add(lStroker);
        lStroker.strokeFrom((Point) pStroker.mPoints.get(0), true);
        prepareDrawingLayers(lBrush);

        int i;
        int size = pStroker.mPoints.size();

        for (i = 1; i < size; i++) {
            Point lPoint2 = pStroker.mPoints.get(i);
            lBrush.restoreBrush(lPoint2.data);
            Rect lRect2;
            if (i < pStroker.mPoints.size() - 1) {
                lRect2 = strokeToCanvasWithBrush(pCanvas, lStroker,
                        lBrush, lPoint2, true, true);
            } else {
                lRect2 = strokeToCanvasWithBrush(pCanvas, lStroker,
                        lBrush, lPoint2, true, false);
            }

            dirtyRect.set(lRect2);
            dirtyRect.intersect(mClipRegion);
            lRect1.union(dirtyRect);

            if (i == pStroker.mPoints.size() - 1) {
                composeWholeStroke(pCanvas, lStroker);
                clearCanvas(mDrawingCanvas, lStroker.mDirtyRect);
            }
        }
        dePrepareDrawingLayers();
        return lRect1;
    }

    public void addStroke(Stroker pStroker) {
        mStrokerList.add(pStroker);
        mCurStrokeIndex = mCurStrokeIndex + 1;
    }


    public void clearPainting() {
        try {


            clearCanvas(mDrawingCanvas, mClipRegion);
            clearCanvas(mCanvas, mClipRegion);
            clearCanvas(mUndoBaselineCanvas, mClipRegion);
            clearCanvas(mComposeCanvas, mClipRegion);

            if (mStrokerList != null) {
                mStrokerList.clear();
                mHisotryStrokeListOfBaseSnapShot.clear();
            }

            clearUndoStack();
            clearRedoStack();

            mCurStrokeIndex = -1;

            if (mBackgroundBitmap == null) {
                backgroundPaint.setColor(mBackgroundColor);
                if (mCanvas != null) {
                    mCanvas.drawRect(mClipRegion, backgroundPaint);
                    mUndoBaselineCanvas.drawRect(mClipRegion, backgroundPaint);
                }
            } else {
                if (mCanvas != null) {
                    if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled())
                        mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, backgroundPaint);

                    if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled())
                        mUndoBaselineCanvas.drawBitmap(mBackgroundBitmap, 0, 0, backgroundPaint);
                }
            }
        } catch (Exception e) {

        }
    }

    public void clearRedoStack() {
        for (int i = 0; i < mDeletedStrokes.size(); i++)
            ((CachedUndoStroke) mDeletedStrokes.get(i)).destroy();

        mDeletedStrokes.clear();
    }

    public Brush createBrush() {
        Brush lBrush = Brush.createBrush(mBrushStyle);
        lBrush.setQuadDecompositor(mQuadCurve);
        lBrush.setPatternManager(mPatternManager);
        lBrush.mPaintingStrokeList = mStrokerList;

        if (mParent != null) {
            lBrush.setScale(mParent.getResources().getDisplayMetrics().density);

            if (mBackgroundColor != -1)
                lBrush.setRandomColorPicker(mRandomBrightColorPicker);
            else
                lBrush.setRandomColorPicker(mRandomDarkColorPicker);
        } else
            lBrush.setScale(1.0F);

        lBrush.setColor(mBrushColor);
        lBrush.setAlpha(mBrushAlpha);
        lBrush.setSize(mBrushSize);
        lBrush.setMode(mBrushKidOrArtistMode);
        lBrush.mBrushFlow = mBrushFlow;

        if (mBrushDemoMode)
            lBrush.mRandomSeed = 1L;

        if (mBrushStyle == 112) {
            if (mBackgroundBitmap != null)
                lBrush.setColor(0);
            else
                lBrush.setColor(mBackgroundColor);
        }
        return lBrush;
    }

    public boolean createCanvas(int pInt1, int pInt2) {
        try {
            if ((mPaintingWidth == pInt1)
                    && (mPaintingHeight == pInt2))
                return false;

            mPaintingWidth = pInt1;
            mPaintingHeight = pInt2;
            mClipRegion = new Rect(0, 0, pInt1, pInt2);
            mDirtyRegion.set(mClipRegion);
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }

            mBitmap = Bitmap.createBitmap(pInt1, pInt2,
                    Bitmap.Config.ARGB_8888);
            mBitmap.setPremultiplied(true);
            mCanvas = new Canvas(mBitmap);

            clearCanvas(mCanvas, mClipRegion);

            if (mDrawingBitmap != null) {
                mDrawingBitmap.recycle();
                mDrawingBitmap = null;
            }

            mDrawingBitmap = Bitmap.createBitmap(pInt1, pInt2,
                    Bitmap.Config.ARGB_8888);
            mDrawingCanvas = new Canvas(mDrawingBitmap);

            clearCanvas(mDrawingCanvas, mClipRegion);

            if (mComposeBitmap != null) {
                mComposeBitmap.recycle();
                mComposeBitmap = null;
            }

            mComposeBitmap = Bitmap.createBitmap(pInt1, pInt2,
                    Bitmap.Config.ARGB_8888);
            mComposeCanvas = new Canvas(mComposeBitmap);

            clearCanvas(mComposeCanvas, mClipRegion);

            if (mReplayBitmap != null) {
                mReplayBitmap.recycle();
                mReplayBitmap = null;
            }
            mReplayBitmap = Bitmap.createBitmap(pInt1, pInt2,
                    Bitmap.Config.ARGB_8888);
            mReplayCanvas = new Canvas(mReplayBitmap);

            clearCanvas(mReplayCanvas, mClipRegion);

            if (mUndoBaselineBitmap != null) {
                mUndoBaselineBitmap.recycle();
                mUndoBaselineBitmap = null;
            }
            mUndoBaselineBitmap = Bitmap.createBitmap(pInt1,
                    pInt2, Bitmap.Config.ARGB_8888);
            mUndoBaselineCanvas = new Canvas(mUndoBaselineBitmap);

            clearCanvas(mUndoBaselineCanvas, mClipRegion);
        } catch (OutOfMemoryError lOutOfMemoryError) {
            lOutOfMemoryError.printStackTrace();
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }

        return true;
    }

    public void deinit() {
        try {
            Log.i(TAG, "deini");

            mStrokerList.clear();
            mStrokerList = null;
            mCurStroker = null;
            mHisotryStrokeListOfBaseSnapShot.clear();
            mHisotryStrokeListOfBaseSnapShot = null;
            clearUndoStack();
            clearRedoStack();
            mPatternManager.finish();
            mPatternManager = null;

            if ((mBackgroundBitmap != null)
                    && (!mBackgroundBitmap.isRecycled())) {
                mBackgroundBitmap.recycle();
                mBackgroundBitmap = null;
            }

            mParent = null;
            mCanvas = null;
            if (mBitmap != null)
                mBitmap.recycle();
            mBitmap = null;
            mReplayCanvas = null;
            if (mReplayBitmap != null)
                mReplayBitmap.recycle();
            mReplayBitmap = null;

            if (mUndoBaselineBitmap != null) {
                mUndoBaselineBitmap.recycle();
                mUndoBaselineBitmap = null;
            }

            mUndoBaselineCanvas = null;

            if (mComposeBitmap != null) {
                mComposeBitmap.recycle();
                mComposeBitmap = null;
            }

            mComposeCanvas = null;

            if (mDrawingBitmap != null) {
                mDrawingBitmap.recycle();
                mDrawingBitmap = null;
            }

            mDrawingCanvas = null;
            mCompositionPaint = null;
            mCurrentBrush = null;
            mReplayPaintingThread = null;
            mRandomBrightColorPicker = null;
            mRandomDarkColorPicker = null;
            mQuadCurve.finish();
            mQuadCurve = null;
        } catch (Exception lException) {
            Log.e("TAG", "Exception lException " + lException.getMessage());
        }
    }

    public Bitmap getBackgroundBitmap() {
        return mBackgroundBitmap;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Brush getBrush() {
        return mCurrentBrush;
    }

    public int getBrushStyle() {
        return mBrushStyle;
    }

    public int getMovieSpeed() {
        return mFastForwardSpeed;
    }

    public Bitmap getPainting() {
        try {
            if (mReplayCanvas != null) {
                mReplayCanvas.save();
                mReplayCanvas.clipRect(mClipRegion);
                drawBackground(mReplayCanvas);
                if (mBitmap != null && !mBitmap.isRecycled())
                    mReplayCanvas.drawBitmap(mBitmap, 0.0F, 0.0F, null);
                mReplayCanvas.restore();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception e 629 " + e.getMessage());
        }
        return mReplayBitmap;
    }

    public int getStrokeCount() {
        return mStrokerList.size();
    }

    public ArrayList getStrokeList() {
        return mStrokerList;
    }

    public void hasSaved() {
        mSaved = true;
    }

    public boolean isEmpty() {
        if ((mStrokerList == null) || (mStrokerList.size() == 0))
            return true;

        return false;
    }

    public boolean isSaved() {
        return mSaved;
    }

    public boolean isUseBackgroundBitmap() {
        return mbHasBackgroundBitmap;
    }

    public Rect lazyStrokestrokeTo(Canvas pCanvas, Stroker pStroker,
                                   Brush pBrush, Point pPoint, boolean pBoolean1,
                                   boolean pBoolean2) {
        Rect lObject;

        if (mBrushStyle == 16)
            return null;

        Canvas lCanvas1;

        if (pBrush.mBrushStyle == 112)
            lCanvas1 = pCanvas;
        else
            lCanvas1 = mDrawingCanvas;

        try {
            pStroker.mDirtyRect.intersect(mClipRegion);

            if ((pBrush.mMustRedrawWholeStrokePath) && (!pStroker.mDirtyRect.isEmpty())) {
                clearCanvas(mDrawingCanvas, pStroker.mDirtyRect);
            }

            lObject = pStroker.lazyStrokeTo(lCanvas1, pPoint);

            if (lObject != null)
                ((Rect) lObject).intersect(mClipRegion);
            else
                return null;

            return lObject;
        } catch (NullPointerException lNullPointerException) {
            lNullPointerException.printStackTrace();
            mDirtyRegion.setEmpty();
            return mDirtyRegion;
        }
    }


    public Rect redoStroke() {
        if (mDeletedStrokes.isEmpty()) {
            redoClicks = redoClicks - 1;

            if (redoClicks <= -1) {
                redoClicks = -1;
                try {
                    PaintActivity.obj_interface.clearAddRemoveStrokeInRedoList();
                    Toast.makeText(mParent, "No more stroke to redo", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }
            return null;
        }

        PaintActivity.obj_interface.addRemoveStrokeInRedoList(false);

        CachedUndoStroke lCachedUndoStroke = (CachedUndoStroke) mDeletedStrokes
                .pop();
        mStrokerList.add(lCachedUndoStroke.stroke);
        mCurStrokeIndex = mCurStrokeIndex + 1;
        mCanvas.save();

        mCanvas.clipRect(lCachedUndoStroke.strokeRegion);
        Paint lPaint = new Paint();
        lPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        if (lCachedUndoStroke.baseSnapShot != null && !lCachedUndoStroke.baseSnapShot.isRecycled())
            mCanvas.drawBitmap(lCachedUndoStroke.baseSnapShot, 0.0F, 0.0F,
                    lPaint);

        for (int m = 0; m < lCachedUndoStroke.restoreStrokeList.size(); m++) {
            undoRedoDrawStrokeOnCanvas(mCanvas, (Stroker) lCachedUndoStroke.restoreStrokeList.get(m),
                    cloneSnapshotHistoryStrokers());
        }

        PaintActivity.obj_interface.hideShowCross(false);
        mCanvas.restore();
        mCachedUndoStrokeList.add(lCachedUndoStroke);
        undoClicks = 0;

        return lCachedUndoStroke.strokeRegion;
    }

    public void resetMovieSpeed() {
        mFastForwardSpeed = 1;
    }

    public void setAlpha(int pInt) {
        mBrushAlpha = pInt;
    }

    public void setBackgroundBitmap(Bitmap pBitmap) {
        if ((mBackgroundBitmap != null)
                && (!mBackgroundBitmap.isRecycled())) {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }

        mBackgroundBitmap = pBitmap;

        if (pBitmap == null)
            mbHasBackgroundBitmap = false;
        else
            mbHasBackgroundBitmap = true;
    }

    public void setBackgroundColor(int pInt) {
        mBackgroundColor = pInt;
    }

    public void setBitmap(Bitmap pBitmap) {
        if (pBitmap != null && !pBitmap.isRecycled()) {
            createCanvas(pBitmap.getWidth(), pBitmap.getHeight());
            mCanvas.drawBitmap(pBitmap, 0.0F, 0.0F, null);
        }
    }


    public void setBrushColor(int pInt) {
        mBrushColor = pInt;
    }


    public int getBrushColor() {
        return mBrushColor;
    }


    public void setBrushMode(int pInt) {
        mBrushKidOrArtistMode = pInt;
    }

    public void setBrushSize(float pFloat) {
        mBrushSize = pFloat;
    }


    public float getBrushSize() {
        return mBrushSize;
    }


    public void setBrushStyle(int pInt) {
        mBrushStyle = pInt;
    }

    public void setDensity(float pFloat1, float pFloat2) {
        mXDensity = pFloat1;
        mYDensity = pFloat2;
        mBrushSize = 5.0F * mXDensity / 240.0F;
    }

    public void setStrokerList(ArrayList<Stroker> pArrayList) {
        mStrokerList = pArrayList;
    }

    public void setUseBackgroundBitmap(boolean pBoolean) {
        mbHasBackgroundBitmap = pBoolean;
    }


    public void showPaintingOnCanvas(Canvas pCanvas, Rect pRect, boolean pBoolean) {
        try {
            if (pRect == null)
                pRect = mClipRegion;

            /*Paint p=new Paint();
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));*/
            if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
                pCanvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
            }
            Bitmap lBitmap2;

            if (!pBoolean) {
                lBitmap2 = mBitmap;
            } else {
                lBitmap2 = mReplayBitmap;
            }


            if (mDrawingCanvasVisible) {
                composeDrawingCanvasToTempCanvas(lBitmap2, pRect);
                pCanvas.save();
                mBitmap.setPremultiplied(true);
                mComposeBitmap.setPremultiplied(true);
                if (mComposeBitmap != null && !mComposeBitmap.isRecycled()) {
//                    pCanvas.drawBitmap(mComposeBitmap, pRect.left, pRect.top, null);

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    int x = (int) pRect.left;
                    int y = (int) pRect.top;
                    int radius = 80;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.WHITE);
                    pCanvas.drawPaint(paint);
                    // Use Color.parseColor to define HTML colors
                    paint.setColor(mCurrentBrush.mBrushColor);
//                    pCanvas.drawCircle(x / 2, y / 2, radius, paint);

                    pCanvas.drawBitmap(mComposeBitmap, 0.0F, 0.0F, paint);
                }

                pCanvas.restore();
                clearCanvas(mComposeCanvas, pRect);
            } else {
                if (lBitmap2 != null && !lBitmap2.isRecycled()) {
                    pCanvas.drawBitmap(lBitmap2, pRect.left, pRect.top, null);
//                    pCanvas.drawBitmap(lBitmap2, 0.0F, 0.0F, null);

                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at onTouch " + e.getMessage());
        }
    }


    public void stopReplayPainting() {
        if (mMovieStatus == 4)
            return;

        mMovieStatus = 4;
        mShallStopReplay = true;
        int i = 1;

        while (i != 0) {
            try {
                mReplayPaintingThread.join();
                i = 0;
            } catch (InterruptedException lInterruptedException) {
            }
        }
    }

    public Rect strokeEnd(float pFloat1, float pFloat2) {
        Rect lRect1 = new Rect();
        try {
            if (mBrushStyle == 16)
                return null;

            mStopPoint.x = (int) pFloat1;
            mStopPoint.y = (int) pFloat2;

            if (mStopPoint.equals(mStartPoint)) {
                pFloat1 += 0.5F;
                pFloat2 += 0.5F;
            }

            lRect1 = strokeToCanvasWithBrush(mCanvas,
                    mCurStroker, mCurrentBrush, new Point(pFloat1,
                            pFloat2), false, false);
            composeWholeStroke(mCanvas, mCurStroker);
            clearCanvas(mDrawingCanvas, mCurStroker.mDirtyRect);
            dePrepareDrawingLayers();
            mCurStrokeIndex = mCurStrokeIndex + 1;
            mSaved = false;
            cacheUndoStroke(mCurStroker, mCurStrokeIndex);
            mCurStroker.finish();

            if (lRect1 == null)
                return null;

            mDirtyRegion.set(lRect1);
        } catch (Exception e) {
            Log.e("TAG", "Exception at strokeEnd " + e.getMessage());
        }
        return lRect1;
    }


    public void strokeFrom(float pFloat1, float pFloat2) {
        if (mBrushStyle == 16) {
            Log.e(TAG, "null brush");
            return;
        }

        clearRedoStack();

        mStartPoint.x = (int) pFloat1;
        mStartPoint.y = (int) pFloat2;
        mCurrentBrush = createBrush();
        mCurStroker = new Stroker();
        mStrokerList.add(mCurStroker);
        mCurStroker.setBrush(mCurrentBrush);
        mCurStroker.strokeFrom(new Point(pFloat1,
                pFloat2), false);
        mDirtyRegion.setEmpty();

        prepareDrawingLayers(mCurrentBrush);
    }


    public void releaseCurrentStroke() {

        try {
            mStartPoint.x = (int) 0;
            mStartPoint.y = (int) 0;
            mDirtyRegion.setEmpty();
            mCurStroker.finish();
            mCurStroker = null;
        } catch (Exception e) {
            Log.e("TAG", "Exception at releaseCurrentStroke " + e.getMessage());
        }
    }


    public Rect strokeTo(float pFloat1, float pFloat2) {
        if (mCurrentBrush != null) {
            mCurrentBrush.setSize(mBrushSize);
            mCurStroker.setBrush(mCurrentBrush);
        }
        return strokeToCanvasWithBrush(mCanvas, mCurStroker,
                mCurrentBrush, new Point(pFloat1,
                        pFloat2), false, true);
    }

    public Rect strokeTo(float pFloat1, float pFloat2, float size) {
        Log.e("TAG", "strokeTo size " + size);
        if (mCurrentBrush != null) {
            mCurrentBrush.setSize(size);
            mCurStroker.setBrush(mCurrentBrush);
        }
        return strokeToCanvasWithBrush(mCanvas, mCurStroker,
                mCurrentBrush, new Point(pFloat1,
                        pFloat2), false, true);
    }


    public Rect strokeToCanvasWithBrush(Canvas pCanvas,
                                        Stroker pStroker, Brush pBrush,
                                        Point pPoint, boolean pBoolean1,
                                        boolean pBoolean2) {
        Rect lObject;

        if (mBrushStyle == 16)
            return null;

        Canvas lCanvas1;

        if (pBrush.mBrushStyle == 112)
            lCanvas1 = pCanvas;
        else
            lCanvas1 = mDrawingCanvas;

        try {
            pStroker.mDirtyRect.intersect(mClipRegion);

            if ((pBrush.mMustRedrawWholeStrokePath)
                    && (!pStroker.mDirtyRect.isEmpty())) {
                clearCanvas(mDrawingCanvas, pStroker.mDirtyRect);
            }

            if (pBoolean2)
                lObject = pStroker.strokeTo(lCanvas1, pPoint,
                        pBoolean1);
            else
                lObject = pStroker.strokeEnd(lCanvas1, pPoint,
                        pBoolean1);

            if (lObject != null)
                ((Rect) lObject).intersect(mClipRegion);
            else
                return null;

            return lObject;
        } catch (NullPointerException lNullPointerException) {
            lNullPointerException.printStackTrace();
            mDirtyRegion.setEmpty();
            return mDirtyRegion;
        } catch (Exception e) {
            Log.e("TAG", "Exception at strokeToCanvasWithBrush " + e.getMessage() + " " + e.toString());
            mDirtyRegion.setEmpty();
            return mDirtyRegion;
        }
    }

    public void syncComposeCanvas() {
        try {
            Paint lPaint = new Paint();
            lPaint.setXfermode(mCopyMode);
            if (mComposeCanvas != null && mBitmap != null && !mBitmap.isRecycled()) {
                mComposeCanvas.drawBitmap(mBitmap, 0.0F, 0.0F, lPaint);
            }
        } catch (Exception lException) {
            Log.e("TAG", "Exception 975" + lException.getMessage());
            // String str = lException.getMessage();
            // FlurryAgent.onError("NullPointerException", str,
            // "2.0.2 Painting syncComposeCanvas");
        }
    }

    public void syncUndoCanvas() {
        try {
            Paint lPaint = new Paint();
            lPaint.setXfermode(mCopyMode);

            if (mUndoBaselineCanvas != null) {
                if (mBitmap != null && !mBitmap.isRecycled())
                    mUndoBaselineCanvas.drawBitmap(mBitmap, 0.0F, 0.0F,
                            lPaint);
            }
        } catch (Exception lException) {
            Log.e("TAG", "Exception 992" + lException.getMessage());
        }
    }

    public Rect undoStroke(Activity _context) {
        if (mCachedUndoStrokeList.size() == 0) {
            undoClicks = undoClicks - 1;

            if (undoClicks <= -1) {
                undoClicks = -1;
                try {
                    Toast.makeText(mParent, "No more stroke to undo", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }
            return null;
        }

        PaintActivity.obj_interface.addRemoveStrokeInRedoList(true);

        int j = mCachedUndoStrokeList.size() - 1;
        CachedUndoStroke lCachedUndoStroke =
                (CachedUndoStroke) mCachedUndoStrokeList.get(j);
        mCachedUndoStrokeList.remove(j);

        mCanvas.save();
        mCanvas.clipRect(lCachedUndoStroke.strokeRegion);
        Paint lPaint = new Paint();
        lPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        if (lCachedUndoStroke.baseSnapShot != null && !lCachedUndoStroke.baseSnapShot.isRecycled())
            mCanvas.drawBitmap(lCachedUndoStroke.baseSnapShot, 0.0F, 0.0F, lPaint);
        ArrayList lArrayList1 = cloneSnapshotHistoryStrokers();
        for (int m = 0; m < lCachedUndoStroke.restoreStrokeList.size() - 1; m++) {
            Rect lRect3 = undoRedoDrawStrokeOnCanvas(mCanvas,
                    (Stroker) lCachedUndoStroke.restoreStrokeList.get(m), lArrayList1);
        }

        mCanvas.restore();

        if (mStrokerList.size() > 0)
            mStrokerList.remove(mStrokerList.size() - 1);

        mCurStrokeIndex = mCurStrokeIndex - 1;
        mDeletedStrokes.push(lCachedUndoStroke);
        redoClicks = 0;
        mSaved = false;

        if (getStrokeList().size() == 0) {
            // new code
            int orientation = _context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                _context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                _context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
           // _context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            PaintActivity.obj_interface.hideShowCross(true);
        }
        return lCachedUndoStroke.strokeRegion;
    }

    class CachedIntermediatePaint {
        public Bitmap bitmap;
        public int drawToIndex;

        private CachedIntermediatePaint() {
        }
    }

    class CachedUndoStroke {
        public Bitmap baseSnapShot;
        public Bitmap regionOrignalSnapshot;
        public ArrayList<Stroker> restoreStrokeList;
        public Stroker stroke;
        public int strokeIndex;
        public Rect strokeRegion;

        private CachedUndoStroke() {
        }

        public void destroy() {
            restoreStrokeList.clear();
            restoreStrokeList = null;
            regionOrignalSnapshot = null;
            baseSnapShot = null;
            restoreStrokeList = null;
            stroke = null;
        }
    }

    public void clearAndUpdateBGPainting() {
        try {


           /* clearCanvas(mDrawingCanvas, mClipRegion);
            clearCanvas(mCanvas, mClipRegion);
            clearCanvas(mUndoBaselineCanvas, mClipRegion);
            clearCanvas(mComposeCanvas, mClipRegion);

            if (mStrokerList != null) {
                mStrokerList.clear();
                mHisotryStrokeListOfBaseSnapShot.clear();
            }

            clearUndoStack();
            clearRedoStack();

            mCurStrokeIndex = -1;*/

            backgroundPaint.setColor(mBackgroundColor);
            if (mCanvas != null) {
                mCanvas.drawRect(mClipRegion, backgroundPaint);
                mUndoBaselineCanvas.drawRect(mClipRegion, backgroundPaint);
            }




            /*if (mBackgroundBitmap == null) {
                backgroundPaint.setColor(mBackgroundColor);
                if (mCanvas != null) {
                    mCanvas.drawRect(mClipRegion, backgroundPaint);
                    mUndoBaselineCanvas.drawRect(mClipRegion, backgroundPaint);
                }
            } else {
                if (mCanvas != null) {
                    if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled())
                        mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, backgroundPaint);

                    if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled())
                        mUndoBaselineCanvas.drawBitmap(mBackgroundBitmap, 0, 0, backgroundPaint);
                }
            }*/
        } catch (Exception e) {
            Log.e("TAGGG", "onClick Select Exception here ? " + e.getMessage(), e);
        }
    }
}

