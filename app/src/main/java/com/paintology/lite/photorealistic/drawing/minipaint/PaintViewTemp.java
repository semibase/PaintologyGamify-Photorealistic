package com.paintology.lite.photorealistic.drawing.minipaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.paintology.lite.photorealistic.drawing.painting.PaintingTemp;

public class PaintViewTemp extends SurfaceView
        implements SurfaceHolder.Callback {
    public static int MAX_SCALE = 50;
    public static int MIN_SCALE = 10;
    private static final float TOUCH_TOLERANCE = 2.0F;
    private static final float ZOOM_THRESHOLD = 5.0F;
    private String TAG = "PaintView";
    RectF dirtyRectFOnScreen;
    Rect dirtyRectOnScreen;
    float[] dxdy;
    float infoBarBottom;
    float infoBarLeft;
    float infoBarRight;
    float infoBarTop;
    int[] location;
    private Context mContext;
    public float mDensity;
    public Handler mHandler;
    public SurfaceHolder mHolder;
    private Matrix mInvMatrix;
    private boolean mIsIdenticalMatrix;
    private Matrix mMatrix;
    private int mOrigScale;
    private float mOrigTx;
    private float mOrigTy;
    private PaintingTemp mPainting;
    private PaintActivity mPaintActivity;
    private float mPivotX = 0.0F;
    private float mPivotY = 0.0F;
    public int mScale = 10;
    private Matrix mTmpMatrix;
    private float mTx;
    private float mTy;
    private float mX;
    private float mY;
    float markHeight;
    private PointF midPoint;
    private PointF newMidPoint;
    private float oldDist;
    RectF paintRectOnPaintSpace;
    RectF paintRectOnScreenSpace;
    float posWindowLeft;
    float posWindowTop;
    float posWindowWidth;
    float posWindowZoomOut;
    float[] touchPoint;
    float zoomBarBgBottom;
    float zoomBarBgHeight;
    float zoomBarBgLeft;
    float zoomBarBgRight;
    float zoomBarBgTop;
    float zoomBarBottom;
    float zoomBarHeight;
    float zoomBarLeft;
    float zoomBarRight;
    float zoomBarTop;
    float zoomBarWidth;

    boolean m_bInitFlag = false;

    public PaintViewTemp(Context pContext, AttributeSet pAttributeSet) {
        super(pContext, pAttributeSet);

        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderOnTop(false); //necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);


        mMatrix = new Matrix();
        mInvMatrix = new Matrix();
        mTmpMatrix = new Matrix();
        location = new int[2];
        touchPoint = new float[2];
        paintRectOnPaintSpace = new RectF();
        paintRectOnScreenSpace = new RectF();
        mDensity = 1.0F;
        dxdy = new float[2];
        midPoint = new PointF();
        newMidPoint = new PointF();

        dirtyRectOnScreen = new Rect();
        dirtyRectFOnScreen = new RectF();
        mHolder = getHolder();
        mHolder.addCallback(this);
        mContext = pContext;
        mPaintActivity = (PaintActivity) pContext;

        mHandler = new Handler();
        setFocusable(true);
    }

    private boolean createPainting(int pInt1, int pInt2) {
        boolean bool = mPainting.createCanvas(getWidth(), getHeight());

        if (bool) {
            mPainting.clearPainting();
            Log.d(TAG, "size changed");
            if (mPaintActivity.shallRecover()) {
                mPainting.setBackgroundBitmap(mPaintActivity.getRecovery());
            }
        }

        reDraw(null);

        return bool;
    }

    private void drawDirtyRect(Canvas pCanvas, Rect pRect) {
        Paint lPaint = new Paint();
        lPaint.setStyle(Paint.Style.STROKE);
        lPaint.setColor(-16777216);
        lPaint.setStrokeWidth(1.5F);

        Path lPath = new Path();
        lPath.addRect(new RectF(pRect), Path.Direction.CW);
        pCanvas.drawPath(lPath, lPaint);
    }

    private void drawZoomDragInfoBar(Canvas pCanvas) {
        setInfoBarLayout(mDensity);
        Paint lPaint = new Paint(1);
        lPaint.setARGB(120, 0, 0, 0);


        pCanvas.save();
        pCanvas.translate(infoBarLeft, infoBarTop);
        pCanvas.drawRoundRect(new RectF(0.0F, 0.0F, zoomBarBgRight, zoomBarBgBottom), 5.0F, 5.0F, lPaint);
        pCanvas.restore();

        lPaint.setARGB(220, 250, 250, 0);
        pCanvas.save();
        pCanvas.translate(infoBarLeft, infoBarTop);
        pCanvas.translate(zoomBarLeft, zoomBarTop);

        pCanvas.drawRoundRect(new RectF(0.0F, 0.0F, zoomBarRight - zoomBarLeft, zoomBarHeight),
                zoomBarHeight / 3.0F, zoomBarHeight / 3.0F, lPaint);

        lPaint.setARGB(220, 250, 250, 250);
        Path lPath = new Path();
        lPath.moveTo(0.0F, 0.0F);
        lPath.lineTo(-markHeight / 2.0F, markHeight);
        lPath.lineTo(markHeight / 2.0F, markHeight);
        lPath.close();

        pCanvas.translate(((mScale - MIN_SCALE) * 1.0F) / (MAX_SCALE - MIN_SCALE) * zoomBarWidth, zoomBarHeight);
        lPaint.setStyle(Paint.Style.FILL);
        pCanvas.drawPath(lPath, lPaint);
        pCanvas.restore();

        pCanvas.save();

        if (!mPaintActivity.mHideAdsView) {
            pCanvas.clipRect(0.0F, 48.0F * mDensity, getWidth(), getHeight());
        }

        pCanvas.translate(posWindowLeft, posWindowTop);
        pCanvas.scale(posWindowZoomOut, posWindowZoomOut);
        lPaint.setARGB(120, 80, 80, 80);
        pCanvas.drawRect(-40.0F, -40.0F, getWidth() + 40, getHeight() + 40, lPaint);
        lPaint.setARGB(180, 200, 200, 200);
        pCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), lPaint);

        Bitmap bmp = mPainting.getBitmap();

        if (bmp != null && !bmp.isRecycled())
            pCanvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()),
                    new Rect(0, 0, getWidth(), getHeight()), null);

        width = getWidth();
        height = getHeight();
        RectF pRectF = new RectF();
        getFocusedRectOnPaint(pRectF);
        lPaint.setARGB(180, 255, 255, 0);
        pCanvas.drawRect(pRectF, lPaint);
        pCanvas.restore();
        invalidate();
        pCanvas.save();
        Log.e("TAGGG", "drawZoomDragInfoBar called mScale " + mScale + " getWidth() " + getWidth() + " getHeight() " + getHeight());
    }

    int width, height;

    private void getFocusedRectOnPaint(RectF pRectF) {
        pRectF.set(new RectF(0.0F, 0.0F, getWidth(), getHeight()));

        if (mScale == 10)
            return;

        Matrix lMatrix = new Matrix();
        mMatrix.invert(lMatrix);
        lMatrix.mapRect(pRectF);
    }

    private void getPivotPoint(float pFloat1, float pFloat2) {
        Matrix lMatrix1 = new Matrix();
        lMatrix1.setScale(mScale / 10.0F, mScale / 10.0F, mPivotX, mPivotY);
        lMatrix1.postTranslate(mTx, mTy);

        Matrix lMatrix2 = new Matrix();
        lMatrix1.invert(lMatrix2);

        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = pFloat1;
        arrayOfFloat[1] = pFloat2;

        lMatrix2.mapPoints(arrayOfFloat);
        mPivotX = arrayOfFloat[0];
        mPivotY = arrayOfFloat[1];

        lMatrix1.mapPoints(arrayOfFloat);
    }

    private boolean isPaintOutScreen(Matrix pMatrix, float[] pArrayOfFloat) {
        boolean nRet = false;

        if (mPaintActivity.mHideAdsView) {
            paintRectOnPaintSpace.set(0.0F, 0.0F, getWidth(), getHeight());
        } else {
            paintRectOnPaintSpace.set(0.0F, 48.0F * mDensity, getWidth(), getHeight());
        }

        pMatrix.mapRect(paintRectOnScreenSpace, paintRectOnPaintSpace);

        if (paintRectOnScreenSpace.left > 0.0F) {
            pArrayOfFloat[0] = 0.0F - paintRectOnScreenSpace.left;
            nRet = true;
        }

        if (paintRectOnScreenSpace.right < getWidth()) {
            pArrayOfFloat[0] = getWidth() - paintRectOnScreenSpace.right;
            nRet = true;
        }

        if (paintRectOnScreenSpace.top > paintRectOnPaintSpace.top) {
            pArrayOfFloat[1] = paintRectOnPaintSpace.top - paintRectOnScreenSpace.top;
            nRet = true;
        }

        if (paintRectOnScreenSpace.bottom < paintRectOnPaintSpace.bottom) {
            pArrayOfFloat[1] = paintRectOnPaintSpace.bottom - paintRectOnScreenSpace.bottom;
            nRet = true;
        }

        invalidate();
        return nRet;
    }

    private boolean isSupportMultiTouch() {
        if (Build.VERSION.SDK_INT >= 5)
            return true;

        return false;
    }

    private void midPoint(PointF pPointF, MotionEvent pMotionEvent) {
        pPointF.set((pMotionEvent.getX(0) + pMotionEvent.getX(1)) / 2.0F,
                (pMotionEvent.getY(0) + pMotionEvent.getY(1)) / 2.0F);
    }

    private boolean neglectPoint(float pFloat1, float pFloat2) {
        if ((Math.abs(pFloat1 - mX) > 2.0F) || (Math.abs(pFloat2 - mY) > 2.0F)) {
            mX = pFloat1;
            mY = pFloat2;
            return false;
        }

        return true;
    }

    private boolean neglectPoint(float pFloat1, float pFloat2, float pFloat3) {
        if ((Math.abs(pFloat1 - mX) > pFloat3) || (Math.abs(pFloat2 - mY) > pFloat3))
            return false;

        return true;
    }

    private void setInfoBarLayout(float pFloat) {
        if (mPaintActivity.mHideAdsView) {
            infoBarTop = 10.0F * pFloat;
            infoBarLeft = 10.0F * pFloat;
        } else {
            infoBarTop = 58.0F * pFloat;
            infoBarLeft = 10.0F * pFloat;
        }

        infoBarRight = getWidth() * 2 / 3 - 10.0F * pFloat;
        zoomBarBgLeft = 0.0F;
        zoomBarBgTop = 0.0F;
        zoomBarBgRight = infoBarRight - infoBarLeft;
        zoomBarBgBottom = 30.0F * pFloat;
        zoomBarBgHeight = zoomBarBgBottom;
        zoomBarLeft = 10.0F * pFloat;
        zoomBarRight = zoomBarBgRight - 10.0F * pFloat;
        zoomBarTop = zoomBarBgHeight / 3.0F;
        zoomBarHeight = 6.0F * pFloat;
        zoomBarWidth = zoomBarRight - zoomBarLeft;
        markHeight = 10.0F * pFloat;
        posWindowZoomOut = 0.1666667F;
        posWindowTop = infoBarTop;
        posWindowLeft = (getWidth() * (1.0F - posWindowZoomOut)) - (50.0F * posWindowZoomOut);
    }

    private float spacing(MotionEvent pMotionEvent) {

        return (float) Math.sqrt((pMotionEvent.getX(0) - pMotionEvent.getX(1)) *
                (pMotionEvent.getX(0) - pMotionEvent.getX(1)) +
                (pMotionEvent.getY(0) - pMotionEvent.getY(1)) *
                        (pMotionEvent.getY(0) - pMotionEvent.getY(1)));
    }

    public void onDraw(Canvas pCanvas) {
        reDraw(null);
    }

    protected void onSizeChanged(int pInt1, int pInt2, int pInt3, int pInt4) {
    }

    public boolean onTouchEvent(MotionEvent pMotionEvent) {
        return super.onTouchEvent(pMotionEvent);
    }


    public boolean onTouchEventForMultiTouch(MotionEvent pMotionEvent) {
        int i;
        if (mPaintActivity.inMovie()) {
            return true;
        }

        int j = pMotionEvent.getAction();
        getLocationOnScreen(location);

        int i1 = (int) pMotionEvent.getRawX() - location[0];
        int i3 = (int) pMotionEvent.getRawY() - location[1];

        if (mPaintActivity.mStatus == 1) {
            if (mPaintActivity.m_bInitFlag) {
                touchPoint[0] = i1;
                touchPoint[1] = i3;
                mInvMatrix.mapPoints(touchPoint);

                i1 = (int) touchPoint[0];
                i3 = (int) touchPoint[1];

                if (j == 0) {
                    mX = i1;
                    mY = i3;
                    mPainting.strokeFrom(i1, i3);
                    mPaintActivity.hideToolbars();
                } else if (j == 1) {
//                    FirebaseUtils.logEvents(mContext, new StringConstants().CANVAS_DRAW_STROKE);
                    reDraw(mPainting.strokeEnd(i1, i3));
                } else if (j == 2) {
                    if (neglectPoint(i1, i3)) {
                        return true;
                    }
                    reDraw(mPainting.strokeTo(i1, i3));
                }
            }
        } else if ((mPaintActivity.mStatus == 5) || (mPaintActivity.mStatus == 4) || (mPaintActivity.mStatus == 3)) {
            switch (pMotionEvent.getAction() & 0xFF) {
                case 1:
                case 6:
                case 3:
                case 4:
                default:
                    break;
                case 0:
                    mPaintActivity.mStatus = 4;
                    mX = i1;
                    mY = i3;
                    break;
                case 5:
                    mPaintActivity.mStatus = 3;
                    oldDist = spacing(pMotionEvent);
                    midPoint(midPoint, pMotionEvent);
                    mPivotX = midPoint.x;
                    mPivotY = midPoint.y;
                    mOrigScale = mScale;
                    break;
                case 2:
                    if (mPaintActivity.mStatus == 4) {
                        if (mScale == 1.0F) {
                            return true;
                        }

                        mTx = i1 - mX;
                        mTy = i3 - mY;
                        mTmpMatrix.set(mMatrix);
                        mTmpMatrix.postTranslate(mTx, mTy);

                        if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                            mTx = mTx + dxdy[0];
                            mTy = mTy + dxdy[1];
                        }

                        mMatrix.postTranslate(mTx, mTy);

                        if (mScale == 10)
                            mMatrix.reset();

                        mMatrix.invert(mInvMatrix);
                        mX = i1;
                        mY = i3;
                        reDraw(null);
                    }

                    if (mPaintActivity.mStatus == 3) {
                        i = 0;

                        try {
                            float f42 = (float) Math.sqrt((pMotionEvent.getX(0) - pMotionEvent.getX(1)) *
                                    (pMotionEvent.getX(0) - pMotionEvent.getX(1)) +
                                    (pMotionEvent.getY(0) - pMotionEvent.getY(1)) *
                                            (pMotionEvent.getY(0) - pMotionEvent.getY(1)));

                            midPoint(newMidPoint, pMotionEvent);
                            float f44 = f42 - oldDist;
                            oldDist = f42;

                            if (f44 > 50.0F)
                                f44 = 50.0F;
                            else if (f44 < -50.0F)
                                f44 = -50.0F;

                            mScale = mOrigScale + (int) ((f44 / 10.0F) / mDensity);

                            if (mScale > MAX_SCALE) {
                                mScale = MAX_SCALE;
                            } else if (mScale < MIN_SCALE) {
                                mScale = MIN_SCALE;
                            }

                            mTmpMatrix.set(mMatrix);
                            mTmpMatrix.postScale(mScale * 1.0F / mOrigScale, mScale * 1.0F / mOrigScale, mPivotX, mPivotY);

                            if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                                mTmpMatrix.postTranslate(dxdy[0], dxdy[1]);
                            }

                            mMatrix.set(mTmpMatrix);

                            if (mScale == 10)
                                mMatrix.reset();

                            mMatrix.invert(mInvMatrix);
                            reDraw(null);
                            mOrigScale = mScale;
                            mX = i1;
                            mY = i3;
                        } catch (IllegalArgumentException lIllegalArgumentException) {
                            mScale = mOrigScale;
                        }
                    }
            }
        } else if (mPaintActivity.mStatus == 8) {
            if ((pMotionEvent.getAction() & 0xFF) == MotionEvent.ACTION_UP) {
                if (mPaintActivity.brushSettingsPopup.isShowing())
                    mPaintActivity.returnWithSelectedBrush();
                mPaintActivity.setColor();
                Log.e("TAGGG", "onTouchEventForMultiTouch got to 8 " + mPaintActivity.mStatus + " isVisible " + ((mPaintActivity.brushSettingsPopup.isShowing())));
            }
        } else if (mPaintActivity.mStatus == 9) {
            if ((pMotionEvent.getAction() & 0xFF) == MotionEvent.ACTION_UP) {
                if (mPaintActivity.brushSettingsPopup.isShowing())
                    mPaintActivity.returnWithSelectedBrush();

                mPaintActivity.setColor();
            }
        }
        System.gc();
        return true;
    }

    public boolean onTouchEventForSingleTouch(MotionEvent pMotionEvent) {
        if (mPaintActivity.inMovie())
            return true;

        int i = pMotionEvent.getAction();
        getLocationOnScreen(location);
        int n = (int) pMotionEvent.getRawX() - location[0];
        int i2 = (int) pMotionEvent.getRawY() - location[1];


        if (mPaintActivity.mStatus == 1) {
            touchPoint[0] = n;
            touchPoint[1] = i2;
            mInvMatrix.mapPoints(touchPoint);

            n = (int) touchPoint[0];
            i2 = (int) touchPoint[1];

            if (i == 0) {
                mX = n;
                mY = i2;
                mPainting.strokeFrom(n, i2);
            } else if (i == 1) {
                mPainting.strokeEnd(n, i2);
                reDraw(null);
            } else if (i == 2) {
                if (!neglectPoint(n, i2)) {
                    reDraw(mPainting.strokeTo(n, i2));
                }
            }
        } else if (mPaintActivity.mStatus == 3) {
            if (i == 0) {
                mX = n;
                mY = i2;
                mPivotX = n;
                mPivotY = i2;
                mOrigScale = mScale;
            } else if (i == 2) {
                if (!neglectPoint(n, i2, 10.0F)) {
                    int i4 = (int) (mDensity / ((float) Math.sqrt((n - mX) * (n - mX) + (i2 - mY) * (i2 - mY)) / 10.0F));

                    if (n > mX)
                        mScale = mOrigScale + i4;
                    else
                        mScale = mOrigScale - i4;

                    if (mScale > MAX_SCALE) {
                        mScale = MAX_SCALE;
                    }
                    if (mScale < MIN_SCALE) {
                        mScale = MIN_SCALE;
                    }

                    mTmpMatrix.set(mMatrix);
                    mTmpMatrix.postScale(mScale * 1.0F / mOrigScale, mScale * 1.0F / mOrigScale, mPivotX, mPivotY);

                    if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                        mTmpMatrix.postTranslate(dxdy[0], dxdy[1]);
                    }

                    mMatrix.set(mTmpMatrix);

                    if (mScale == 10)
                        mMatrix.reset();

                    mMatrix.invert(mInvMatrix);
                    reDraw(null);
                    mOrigScale = mScale;
                    mX = n;
                    mY = i2;
                }
            }

        } else if (mPaintActivity.mStatus == 4) {
            if (i == 0) {
                mX = n;
                mY = i2;
            } else if (((i == 2) || (i == 1)) && (mScale != 1.0F)) {
                mTx = n - mX;
                mTy = i2 - mY;
                mTmpMatrix.set(mMatrix);
                mTmpMatrix.postTranslate(mTx, mTy);

                if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                    mTx = mTx + dxdy[0];
                    mTy = mTy + dxdy[1];
                }

                mMatrix.postTranslate(mTx, mTy);

                if (mScale == 10)
                    mMatrix.reset();

                mMatrix.invert(mInvMatrix);
                mX = n;
                mY = i2;
                reDraw(null);
            }
        } else if (mPaintActivity.mStatus == 8) {
            if ((pMotionEvent.getAction() & 0xFF) == MotionEvent.ACTION_UP) {
                if (mPaintActivity.brushSettingsPopup.isShowing())
                    mPaintActivity.returnWithSelectedBrush();

                mPaintActivity.setColor();
            }
        } else if (mPaintActivity.mStatus == 9) {
            if ((pMotionEvent.getAction() & 0xFF) == MotionEvent.ACTION_UP) {
                if (mPaintActivity.brushSettingsPopup.isShowing())
                    mPaintActivity.returnWithSelectedBrush();

                mPaintActivity.setColor();
            }
        }
        return true;
    }

    public void reDraw(Rect pRect) {

        synchronized (mHolder) {
            if (mPainting == null || mPainting.getBitmap() == null)
                return;

            try {
                Canvas lCanvas = mHolder.lockCanvas();
                if (lCanvas == null)
                    return;

                lCanvas.save();

//                mPainting.showPaintingOnCanvas(lCanvas, null, false);
                lCanvas.restore();
//                if ((mPaintor.mStatus == 4) || (mPaintor.mStatus == 3)) {
//                    drawZoomDragInfoBar(lCanvas);
//                    mPaintor.mZoomBtn.invalidate();
//                }
                mHolder.unlockCanvasAndPost(lCanvas);
            } catch (Exception e) {

            }
        }
    }

    public void resetMatrix() {
        mScale = 10;
        mTx = 0.0F;
        mTy = 0.0F;
        mMatrix.reset();
        mInvMatrix.reset();
    }

    public void setPainting(PaintingTemp pPainting) {
        mPainting = pPainting;
    }

    public void surfaceChanged(SurfaceHolder pSurfaceHolder, int pInt1, int pInt2, int pInt3) {
        reDraw(null);
    }

    public void surfaceCreated(SurfaceHolder pSurfaceHolder) {
        int i = getWidth();
        int j = getHeight();
        reDraw(null);
    }

    public void surfaceDestroyed(SurfaceHolder pSurfaceHolder) {
        mPaintActivity.stopReplayPaint();
    }


    public boolean onTouchEventForMultiTouchTemp(float rawX, float rawY, int j) {


        if (mPaintActivity.inMovie()) {
            return true;
        }

//        getLocationOnScreen(location);

        float i1 = rawX - location[0];
        float i3 = rawY - location[1];


        touchPoint[0] = i1;
        touchPoint[1] = i3;


//        mInvMatrix.mapPoints(touchPoint);

        i1 = (int) touchPoint[0];
        i3 = (int) touchPoint[1];

        if (j == 0) {
            mX = i1;
            mY = i3;
            mPainting.strokeFrom(i1, i3);
        } else if (j == 1)
            reDraw(mPainting.strokeEnd(i1, i3));
        else if (j == 2) {
            if (neglectPoint(i1, i3)) {
                return true;
            }
            reDraw(mPainting.strokeTo(i1, i3));
        }

        System.gc();
        return true;
    }

}
