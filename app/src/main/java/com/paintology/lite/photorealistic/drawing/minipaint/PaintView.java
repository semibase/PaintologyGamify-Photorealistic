

package com.paintology.lite.photorealistic.drawing.minipaint;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_OUTSIDE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_UP;

import android.app.Activity;
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
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class PaintView extends SurfaceView
        implements SurfaceHolder.Callback {
    public int MAX_SCALE = 100;
    public int MIN_SCALE = 10;
    private String TAG = "PaintView";
    RectF dirtyRectFOnScreen;
    Rect dirtyRectOnScreen;
    float[] dxdy;
    float infoBarLeft;
    float infoBarRight;
    float infoBarTop;
    int[] location;
    int[] locationTemp;
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
    private Painting mPainting;
    private PaintActivity mPaintActivity;
    private float mPivotX = 0.0F;
    private float mPivotY = 0.0F;
    public int mScale = 10;
    private Matrix mTmpMatrix;
    private float mTx;
    private float mTy;
    //    private float mX;
//    private float mY;
    float markHeight;
    private PointF midPoint;
    private PointF newMidPoint;
    //    private float oldDist;
    public RectF paintRectOnPaintSpace;
    public RectF paintRectOnScreenSpace;

    float[] touchPoint;
    float[] touchPointTemp;


    float f44 = 0;
    float f42 = 0;

    boolean m_bInitFlag = false;

    StringConstants constants = new StringConstants();


    public ScaleGestureDetector mScaleDetector;
    Context pContext;


    public PaintView(Context pContext, AttributeSet pAttributeSet) {
        super(pContext, pAttributeSet);

        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderOnTop(false); //necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);

        this.pContext = pContext;
        mScaleDetector = new ScaleGestureDetector(pContext, mScaleListener);

        _toast = Toast.makeText(pContext, getResources().getString(R.string.zoom_lock_msg), Toast.LENGTH_SHORT);

        mMatrix = new Matrix();
        mInvMatrix = new Matrix();
        mTmpMatrix = new Matrix();
        location = new int[2];
        locationTemp = new int[2];
        touchPoint = new float[2];
        touchPointTemp = new float[2];
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


        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
//        mPaint.setColor(mPainting.getBrushColor());
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
        setFocusable(true);
    }


    int width, height;

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

    private boolean neglectPoint(float pFloat1, float pFloat2) {
        if ((Math.abs(pFloat1 - mX) > 2.0F) || (Math.abs(pFloat2 - mY) > 2.0F)) {
            mX = pFloat1;
            mY = pFloat2;
            return false;
        }

        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       /* try {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                canvas.drawPath(mPath, mPaint);
                canvas.drawPath(circlePath, circlePaint);
            }
        } catch (Exception e) {
            Log.e("TAG", "onDraw Called " + e.getMessage());
        }*/
        Log.e("TAG", "OnDraw Called");
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            //Shift the image to the center of the view
            //Get the center point for future scale and rotate transforms
            mPivotX = w / 2;
            mPivotY = h / 2;
        }
       /* mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);*/
    }

    float radious = 30;

    boolean isLongClicked = false;

    public boolean onTouchEvent(MotionEvent event) {
        try {

            if (mPaintActivity.iv_gps_icon.getVisibility() == VISIBLE)
                return true;
            try {
                if (!PaintActivity.obj_interface.isIndicatorVisible()) {
                    if (!m_bInitFlag) {
                        if (mPaintActivity.m_bInitFlag) {
                            if (mPaintActivity.mScreenWidth > mPaintActivity.mScreenHeight) {
                                Canvas lCanvas = mHolder.lockCanvas();

                                if (lCanvas != null) {
                                    mHolder.unlockCanvasAndPost(lCanvas);
                                    m_bInitFlag = true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception onTouchEvent " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at check " + e.getMessage());
        }
//        Log.e("TAG", "IS Long Clicked Called " + isLongClicked);
        return onTouchEventForSingleTouch(event);
    }

    final Handler handler_zoom = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            Log.e("TAG", "Long press!");
            Toast.makeText(mContext, "Long Press!", Toast.LENGTH_SHORT).show();
            isStopAutoZoom = false;
            startAutoZoom();
        }
    };

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;

    Context context;
    private Paint circlePaint;
    private Path circlePath;

    private Paint mPaint;


    StringBuilder stroke;

    long _time = 0;

//    boolean isZoomed = false;


    public ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // ScaleGestureDetector calculates a scale factor based on whether
            // the fingers are moving apart or together

            if (mScale == MIN_SCALE) {
                mScale += 1;
            }
            float scaleFactor = detector.getScaleFactor();
            //Pass that factor to a scale for the image
            mMatrix.postScale(scaleFactor, scaleFactor, mPivotX, mPivotY);
            Log.e("TAG", "ScaleGestureDetector called scaleFactor " + scaleFactor + " mPivotX " + mPivotX + " mPivotY " + mPivotY);
            if (mScale == MIN_SCALE)
                mMatrix.reset();

            mMatrix.invert(mInvMatrix);
            reDraw(null);
            return true;
        }
    };

    Handler _handler = null;
    Runnable _runnable = null;
    boolean isStopAutoZoom = false;

    void startAutoZoom() {

        _handler = new Handler();
        _runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("TAG", "startAutoZoom called isStopAutoZoom " + isStopAutoZoom);
                if (!isStopAutoZoom) {
                    try {

//                        mScale = mOrigScale + (int) ((f44 / 10.0F) / mDensity);
                        float scaleFactor = 1.1f;
                        //Pass that factor to a scale for the image
                        mMatrix.postScale(scaleFactor, scaleFactor, mPivotX, mPivotY);
                        Log.e("TAG", "ScaleGestureDetector called scaleFactor " + scaleFactor + " mPivotX " + mPivotX + " mPivotY " + mPivotY + " mScale " + mScale);

                        if (mScale == 10) {
                            mMatrix.reset();
                        }
                        mMatrix.invert(mInvMatrix);
                        reDraw(null);
                        mPivotX += 2;
                        mPivotY += 2;
                        postDelayed(this::run, 100);
                        if (mScale <= MAX_SCALE)
                            mScale += 1;
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at auto zoom " + e.getMessage());
                    }
                } else {
                    isStopAutoZoom = false;
                    removeCallbacks(this::run);
                    Log.e("TAG", "Callback Removed ");
                }
            }
        };
        _handler.postDelayed(_runnable, 100);
    }


    void stopAutoZoom() {
        if (_handler != null && _runnable != null) {
            Log.e("TAG", "ScaleGestureDetector callback removed ");
            isStopAutoZoom = true;
            _handler.removeCallbacks(_runnable);
        }
    }


    public int currentScale() {
        Log.e("TAG", "Current Scale " + mScale);
        return mScale;
    }

    public void resetCanvas() {
        try {
            degrees = 0;
            mLastAngle = 0;
            mScale = MIN_SCALE;
            mOrigScale = MIN_SCALE;
            mTmpMatrix.postScale(mScale * 1.0F / mOrigScale, mScale * 1.0F / mOrigScale, mPivotX, mPivotY);
            Log.e("TAG", "resetCanvas mOrigScale " + mOrigScale + " mPivotX " + mPivotX + " mPivotY " + mPivotY + " mScale " + mScale);
            if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                mTmpMatrix.postTranslate(dxdy[0], dxdy[1]);
            }

            mMatrix.set(mTmpMatrix);

            if (mScale == MIN_SCALE)
                mMatrix.reset();

           /* mPaintor.mZoomBtn.setRotation(0);
            mPaintor.mZoomBtn.invalidate();*/

            mPaintActivity.iv_plus_zoom.setImageResource(R.drawable.zoom_normal);
            mPaintActivity.iv_plus_zoom.setRotation(0);
            mPaintActivity.iv_plus_zoom.invalidate();

            mPaintActivity.view_zoom_indicator.setVisibility(GONE);

            mMatrix.invert(mInvMatrix);
            reDraw(null);

            Log.e("TAG", "Reset Canvas Logs " + mPaintActivity.Current_Mode);
            if (!mPaintActivity.Current_Mode.equalsIgnoreCase("Edit Paint") && !mPaintActivity.Current_Mode.equalsIgnoreCase("Reload Painting") && !mPaintActivity.Current_Mode.equalsIgnoreCase("YOUTUBE_TUTORIAL")) {
                mPaintActivity.iv_selected_image.setVisibility(GONE);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    private double getDegreesFromTouchEvent(float x, float y) {
        double delta_x = x - (width) / 2;
        double delta_y = (height) / 2 - y;
        double radians = Math.atan2(delta_y, delta_x);
        Log.e("TAG", "getDegreesFromTouchEvent width " + width + " height " + height);
        return Math.toDegrees(radians);
    }


    float oldDist = 1f;

    private int mLastAngle = 0;

    float deltaX = 0;
    double radians = 0;
    float deltaY = 0;
    int degrees = 0;
    Toast _toast;


    private float mX_new, mY_new;


    int current_color;

    private long mLastClickTime = 0;


//    Handler _longClickHandler;
//    Runnable _longClickRunnable;
//    int counter = 0;


    /*final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            isStopAutoZoom = false;
            startAutoZoom();
            Log.e("TAG", "onTouchEventForSingleTouch Long press!");
        }
    };*/

    int count = 0;


    public boolean isPartialStroke = false;
    float _spotX, _spotY;


    int count_translate = 0;

    public boolean onTouchEventForSingleTouch(MotionEvent pMotionEvent) {

        if (mPaintActivity.inMovie()) {
            return true;
        }
        getLocationOnScreen(location);
        Log.e("TAG", "OnTouch called Action " + pMotionEvent.getAction());
        int j = pMotionEvent.getAction();
        getLocationOnScreen(location);
        float i1 = (int) pMotionEvent.getRawX() - location[0];
        float i3 = (int) pMotionEvent.getRawY() - location[1];
        count++;
        if (mPaintActivity.mStatus == 9) {
            if ((pMotionEvent.getAction() & 0xFF) == MotionEvent.ACTION_DOWN) {
                if (mPaintActivity.brushSettingsPopup.isShowing())
                    mPaintActivity.returnWithSelectedBrush();
            }
        }
        current_color = mPainting.getBrushColor();
        try {
            deltaX = pMotionEvent.getX(0) - pMotionEvent.getX(1);
            deltaY = pMotionEvent.getY(0) - pMotionEvent.getY(1);
            radians = Math.atan(deltaY / deltaX);
            //Convert to degrees
            degrees = (int) (radians * 180 / Math.PI);
        } catch (Exception e) {

        }
        try {
            if (mPaintActivity.mStatus == mPaintActivity.status_block && pMotionEvent.getPointerCount() >= 2) {
                if (!_toast.getView().isShown()) {
                    _toast.show();
                }
                _time = System.currentTimeMillis();
                Log.e("TAG", "REturn Called on Touch 609");
                return true;
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at onTouch " + e.getMessage());
        }

        if (pMotionEvent.getPointerCount() == 1 || mPaintActivity.mStatus == mPaintActivity.status_block) {
            Log.e("TAG", "Time Difference " + (System.currentTimeMillis() - _time));
            if (mPaintActivity.m_bInitFlag && (System.currentTimeMillis() - _time) >= 100) {
                touchPoint[0] = i1;
                touchPoint[1] = i3;
                mInvMatrix.mapPoints(touchPoint);
                i1 = (int) touchPoint[0];
                i3 = (int) touchPoint[1];
                if (j == MotionEvent.ACTION_DOWN) {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    stroke = new StringBuilder();
                    mX = i1;
                    mY = i3;
                    mX_new = i1;
                    mY_new = i3;

                    if (mPaintActivity.switch_singleTap.isChecked()) {
                        mPainting.strokeFrom(i1, i3);
                        reDraw(mPainting.strokeTo(i1, i3));
                        mPainting.strokeEnd(i1, i3);
                        stroke.append(i1 + "," + i3);
                        PaintActivity.obj_interface.addStroke(stroke + "");
                        Log.e("TAG", "REturn Called on Touch 637");
                        return true;
                    }
                    Log.e("TAG", "Stroke Drawing Started from " + i1 + " " + i3);
                    Log.e("TAG", "TouchEvent Action Down " + pMotionEvent.getPointerCount());

                    mPainting.strokeFrom(i1, i3);
                    isPartialStroke = true;
                    _spotX = i1;
                    _spotY = i3;
                    mPaintActivity.hideToolbars();
                    stroke.append(i1 + "," + i3 + "|");
                }
                else if (j == MotionEvent.ACTION_UP) {
                    try {
                        Log.e("TAG", "TouchEvent Action Up " + pMotionEvent.getPointerCount());
                        isPartialStroke = false;
                        if (mPaintActivity.switch_singleTap.isChecked() && SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                            invalidate();
                            Log.e("TAG", "REturn Called on Touch 647");
                            return true;
                        }
                        if (mPaintActivity._switch_line.isChecked()) {
                            reDraw(mPainting.strokeTo(i1, i3));
                            invalidate();
                        }
                        Log.e("TAG", "Stroke Drawing End to " + i1 + " " + i3);
                        reDraw(mPainting.strokeEnd(i1, i3));
                        invalidate();
                        stroke.append(i1 + "," + i3);
                        PaintActivity.obj_interface.addStroke(stroke + "");
//                        if (mPaintor._switch_smooth.isChecked()) {
//                            StringTokenizer tokens = new StringTokenizer(stroke.toString(), "|");
//                            ArrayList<String> lst_strokeAxis = new ArrayList<>();
//                            do {
//                                lst_strokeAxis.add(tokens.nextToken());
//                            } while (tokens.hasMoreTokens());
//                            int index_stroke = 0;
//                            boolean needToApplyFormulla = false;
//                            float cForY = 0f;
//                            float cForX = 0f;
//
//                            for (index_stroke = 0; index_stroke < lst_strokeAxis.size(); index_stroke++) {
//                                try {
//                                    String[] separated = lst_strokeAxis.get(index_stroke).split(",");
//                                    float x = Float.parseFloat(separated[0]);
//                                    float y = Float.parseFloat(separated[1]);
//                                    if (index_stroke == 0) {
//                                        mPainting.strokeFrom(x, y);
//                                        needToApplyFormulla = true;
//                                        cForY = y;
//                                        cForX = x;
//
//                                    } else if (index_stroke == lst_strokeAxis.size() - 1) {
//                                        reDraw(mPainting.strokeEnd(x, y));
//
//                                    } else {
//                                        if (needToApplyFormulla) {
//                                            needToApplyFormulla = false;
//                                            float b1 = 0, b2 = 0;
//                                            String y1 = lst_strokeAxis.get(index_stroke - 1);
//                                            String y2 = lst_strokeAxis.get(index_stroke + 1);
//
//                                            String[] sep = y1.split(",");
//                                            b1 = Float.parseFloat(sep[1]);
//                                            float x1 = Float.parseFloat(sep[0]);
//
//                                            sep = y2.split(",");
//                                            b2 = Float.parseFloat(sep[1]);
//                                            float x2 = Float.parseFloat(sep[0]);
//
//                                            float midY = ((b2 - b1) / 2) + cForY;
//                                            float midx = ((x2 - x1) / 2) + cForX;
//                                            reDraw(mPainting.strokeTo(midx, midY));
//                                        } else {
//                                            needToApplyFormulla = true;
//                                            reDraw(mPainting.strokeTo(x, y));
//                                            cForY = y;
//                                            cForX = x;
//                                        }
//                                    }
//                                    invalidate();
//                                } catch (Exception e) {
//                                    Log.e("TAG", "Exception at post draw " + e.getMessage());
//                                }
//                            }
//                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at Action Up " + e.toString());
                    }
                } else if (j == ACTION_MOVE) {
                    if (mPaintActivity.switch_singleTap.isChecked() && SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                        invalidate();
                        Log.e("TAG", "REturn Called on Touch 721");
                        return true;
                    }
                    try {
                        int dx = (int) Math.abs(i1 - mX_new);
                        int dy = (int) Math.abs(i3 - mY_new);
                        int _output = (dx / dy);
                        Log.e("TAG", "TouchEvent Action Move " + pMotionEvent.getPointerCount());
                        if (mPaintActivity.TOUCH_TOLERANCE == 0 || mPaintActivity.TOUCH_TOLERANCE_1 == 0 || !mPaintActivity._switch_line.isChecked() || (_output >= mPaintActivity.TOUCH_TOLERANCE && _output <= mPaintActivity.TOUCH_TOLERANCE_1)) {
                            mX_new = i1;
                            mY_new = i3;
                            if (stroke == null) {
                                stroke = new StringBuilder();
                            }
                            stroke.append(i1 + "," + i3 + "|");
                            if (neglectPoint(i1, i3)) {
                                Log.e("TAG", "REturn Called on Touch 742");
                                return true;
                            }
                            reDraw(mPainting.strokeTo(i1, i3));
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at event " + e.getMessage());
                    }
                }
            }
            // Include half the stroke width to avoid clipping.
        } else if (pMotionEvent.getPointerCount() == 3) {
            mScaleDetector.onTouchEvent(pMotionEvent);
        } else {
            Log.e("TAG", "OnTouchEvent isPartialStroke " + isPartialStroke);
            if (isPartialStroke) {
                isPartialStroke = false;
                /*Toast.makeText(mContext, "Stroke Covered", Toast.LENGTH_SHORT).show();*/
                reDraw(mPainting.strokeTo(_spotX, _spotY));
                reDraw(mPainting.strokeEnd(_spotX, _spotY));
                mPainting.undoStroke((Activity) pContext);
                mPainting.releaseCurrentStroke();
            }

            switch (pMotionEvent.getAction() & 0xFF) {
                case ACTION_UP: {
                    count_translate = 0;
                }
                break;
                case MotionEvent.ACTION_POINTER_UP:
                    // recycle remaining points
                    mLastAngle = degrees;
                    count_translate = 0;
                    if (pMotionEvent.getPointerCount() == 2) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(mContext, constants.canvas_pinch_zoom, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(mContext, constants.canvas_pinch_zoom);
                    }
                    break;
                case ACTION_CANCEL:
                case ACTION_OUTSIDE:
                default:
                    break;
                case MotionEvent.ACTION_DOWN:
                    mPaintActivity.mStatus = 4;
                    mX = i1;
                    mY = i3;
                    break;
                case ACTION_POINTER_DOWN:
                    mPaintActivity.mStatus = 3;
                    oldDist = spacing(pMotionEvent);
                    midPoint(midPoint, pMotionEvent);
                    mPivotX = midPoint.x;
                    mPivotY = midPoint.y;
                    mOrigScale = mScale;
                    break;
                case ACTION_MOVE:

//                    Log.e("TAG", "PaintView Logs Translate mTx " + mTx + " mTy " + mTy + " i1 " + i1 + " i3 " + i3 + " f44 " + f44 + " count_translate " + count_translate);
                    if (mPaintActivity.mStatus == 3 && count_translate >= 5) {
                        if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                            mTx = mTx + dxdy[0];
                            mTy = mTy + dxdy[1];
                        }
                        mTx = ((i1 - mX));
                        mTy = ((i3 - mY));
                        mTmpMatrix.set(mMatrix);
                        mTmpMatrix.postTranslate(mTx, mTy);
                        mMatrix.postTranslate(mTx, mTy);
                        if (mScale == MIN_SCALE) {
                            mMatrix.reset();
                        }
                        mMatrix.invert(mInvMatrix);
                        mX = i1;
                        mY = i3;
                    }


                    if (mPaintActivity.mStatus == 3) {
                        try {

                            //Prevent move canvas on second time touch two finger.
                            count_translate++;
                            f42 = (float) Math.sqrt((pMotionEvent.getX(0) - pMotionEvent.getX(1)) *
                                    (pMotionEvent.getX(0) - pMotionEvent.getX(1)) +
                                    (pMotionEvent.getY(0) - pMotionEvent.getY(1)) *
                                            (pMotionEvent.getY(0) - pMotionEvent.getY(1)));
                            midPoint(newMidPoint, pMotionEvent);
                            f44 = f42 - oldDist;
                            oldDist = f42;
                            if (f44 > 50.0F)
                                f44 = 50.0F;
                            else if (f44 < -50.0F)
                                f44 = -50.0F;
                            mScale = mOrigScale + (int) ((f44 / MIN_SCALE) / mDensity);

                            if (mScale > MAX_SCALE) {
                                mScale = MAX_SCALE;
                            } else if (mScale < MIN_SCALE) {
                                mScale = MIN_SCALE;
                            }
                            mTmpMatrix.set(mMatrix);
                            mTmpMatrix.postScale(mScale * 1.0F / mOrigScale, mScale * 1.0F / mOrigScale, mPivotX, mPivotY);

                            Log.e("TAG", "OnTouch ScaleValue " + (mScale * 1.0F / mOrigScale));
                           /* if (isPaintOutScreen(mTmpMatrix, dxdy)) {
                                mTmpMatrix.postTranslate(dxdy[0], dxdy[1]);
                            }*/

                            mMatrix.set(mTmpMatrix);

                            if (mScale == MIN_SCALE) {
                                mMatrix.reset();
                            }
                            mMatrix.invert(mInvMatrix);
                            mOrigScale = mScale;
                            mX = i1;
                            mY = i3;

                            _time = System.currentTimeMillis();

                            if ((degrees - mLastAngle) > 45) {
                                //Going CCW across the boundary
                                mTmpMatrix.postRotate(-5, mPivotX, mPivotY);
                            } else if ((degrees - mLastAngle) < -45) {
                                //Going CW across the boundary
                                mTmpMatrix.postRotate(5, mPivotX, mPivotY);
                            } else {
                                //Normal rotation, rotate the difference
                                mTmpMatrix.postRotate(degrees - mLastAngle, mPivotX, mPivotY);
                            }
                            mPaintActivity.iv_selected_image.setVisibility(VISIBLE);
                            mPaintActivity.iv_plus_zoom.setImageResource(R.drawable.zoom_dash);
                            mPaintActivity.iv_plus_zoom.setRotation(degrees);
                            mPaintActivity.iv_plus_zoom.invalidate();

                            mLastAngle = degrees;
                            mMatrix.set(mTmpMatrix);

                            if (mScale == MIN_SCALE) {
                                mPaintActivity.mZoomBtn.setRotation(0);
                                mPaintActivity.mZoomBtn.invalidate();
                                mPaintActivity.iv_plus_zoom.setRotation(0);
                                mPaintActivity.iv_plus_zoom.setImageResource(R.drawable.zoom_normal);
                                mPaintActivity.iv_plus_zoom.invalidate();
                                mMatrix.reset();

                                Log.e("TAG", "Reset Canvas Logs " + mPaintActivity.Current_Mode);
                                if (!mPaintActivity.Current_Mode.equalsIgnoreCase("Edit Paint") && !mPaintActivity.Current_Mode.equalsIgnoreCase("Reload Painting") && !mPaintActivity.Current_Mode.equalsIgnoreCase("YOUTUBE_TUTORIAL")) {
                                    mPaintActivity.iv_selected_image.setVisibility(GONE);
                                }
                            }
                            if ((degrees >= 88 && degrees <= 90) || (degrees >= 0 && degrees <= 3)) {
                                mPaintActivity.iv_plus_zoom.setImageResource(R.drawable.zoom_normal);
                                mPaintActivity.iv_plus_zoom.invalidate();
                            }
                            mMatrix.invert(mInvMatrix);
                            reDraw(null);
                            mOrigScale = mScale;

                        } catch (IllegalArgumentException lIllegalArgumentException) {
                            mScale = mOrigScale;
                        }
                    }
            }
        }
        System.gc();
        return true;
    }


    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    public void reDraw(Rect pRect) {
        Log.e("TAG", "reDraw called mstatus " + mPaintActivity.mStatus);
        synchronized (mHolder) {
            if (mPainting == null || mPainting.getBitmap() == null)
                return;
            try {
                Canvas lCanvas = mHolder.lockCanvas();
                if (lCanvas == null)
                    return;
                lCanvas.save();

                try {
                    mPaintActivity.tv_zoom_per.setText((mScale * 10) + "%");
                    if ((mScale * 10) <= 100)
                        mPaintActivity.view_zoom_indicator.setVisibility(GONE);
                    else if (mPaintActivity.view_zoom_indicator.getVisibility() != VISIBLE) {
                        mPaintActivity.view_zoom_indicator.setVisibility(VISIBLE);
                    }
                } catch (Exception e) {

                }

                if (mScale != MIN_SCALE) {
                    lCanvas.setMatrix(mMatrix);
                    /*lCanvas.drawColor(
                            Color.TRANSPARENT,
                            PorterDuff.Mode.CLEAR);*/

                    lCanvas.drawColor(getResources().getColor(R.color.dull_white));
                    mPaintActivity.iv_selected_image.setScaleType(ImageView.ScaleType.MATRIX);
                    mPaintActivity.iv_selected_image.setImageMatrix(mMatrix);
                    mPaintActivity.iv_selected_image.invalidate();

                    mPaintActivity.iv_temp_traced.setScaleType(ImageView.ScaleType.MATRIX);
                    mPaintActivity.iv_temp_traced.setImageMatrix(mMatrix);
                    mPaintActivity.iv_temp_traced.invalidate();
                } else {
                    mPaintActivity.iv_selected_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mPaintActivity.iv_temp_traced.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                mPainting.showPaintingOnCanvas(lCanvas, null, false);
                lCanvas.restore();
//                if ((mPaintor.mStatus == 4) || (mPaintor.mStatus == 3)) {
//                drawZoomDragInfoBar(lCanvas);
                mPaintActivity.mZoomBtn.invalidate();

//                mPaintor.mZoomBtn.invalidate();
                mHolder.unlockCanvasAndPost(lCanvas);
                invalidate();
            } catch (Exception e) {
                Log.e("TAG", "Exception 694" + e.getMessage());
            }
        }
    }

    public void resetMatrix() {

        degrees = 0;
        mLastAngle = 0;
        mScale = MIN_SCALE;
        mTx = 0.0F;
        mTy = 0.0F;
        mMatrix.reset();
        mInvMatrix.reset();
    }


    public void setPainting(Painting pPainting) {
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


}
