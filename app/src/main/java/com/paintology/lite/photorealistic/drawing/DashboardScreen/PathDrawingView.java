package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.paintology.lite.photorealistic.drawing.R;

import java.util.ArrayList;

public class PathDrawingView extends View implements View.OnTouchListener {


    public static final int LINE = 1;
    public static final int RECTANGLE = 3;
    public static final int SQUARE = 4;
    public static final int CIRCLE = 5;
    public static final int TRIANGLE = 6;
    public static final int SMOOTHLINE = 2;

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint mPaint;
    int currentSize = 10;
    public int mCurrentShape = 2;
    protected boolean isDrawing = false;

    Boolean newAdded = false;
    Boolean allClear = false;


    private ArrayList<Bitmap> bitmap = new ArrayList<>();
    private ArrayList<Bitmap> undoBitmap = new ArrayList<>();
    public final int UndoLimit = 10;


    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    float scalediff;

    int startwidth;
    int startheight;
    float dx = 0, dy = 0, x = 0, y = 0;
    float angle = 0;
    Bitmap emptyBitmap;

    float scale = 1;

    public PathDrawingView(Context context) {
        super(context);
        this.context = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(currentSize);
        mPath = new Path();
        mBitmapPaint = new Paint();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setColor(Color.BLUE);
        mBitmapPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        mBitmapPaint.setStrokeWidth(currentSize);
        mPaint.setMaskFilter(null);
        mBitmapPaint.setMaskFilter(null);
        setBackgroundColor(context.getResources().getColor(R.color.dull_white));

        setOnTouchListener(this::onTouch);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        emptyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.setMatrix(matrix);

        Log.e("TAG", "onDraw ClipBounds clip ");
        PathDrawingActivity._obj_interface.hideLayout();
        if (isDrawing) {
            switch (mCurrentShape) {
                case LINE:
                    onDrawLine(canvas);
                    break;
                case RECTANGLE:
                    onDrawRectangle(canvas);
                    break;
                case SQUARE:
                    onDrawSquare(canvas);
                    break;
                case CIRCLE:
                    onDrawCircle(canvas);
                    break;
                case TRIANGLE:
                    onDrawTriangle(canvas);
                    break;
            }
        }
        mPaint.setStrokeWidth(currentSize);
        mBitmapPaint.setStrokeWidth(currentSize);
    }

    private void onDrawSquare(Canvas canvas) {
        onDrawRectangle(canvas);
    }

    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas, mPaint);
    }

    private void drawRectangle(Canvas canvas, Paint paint) {
        Log.e("TAG", "drawRectangle called");
        float right = mStartX > endX ? mStartX : endX;
        float left = mStartX > endX ? endX : mStartX;
        float bottom = mStartY > endY ? mStartY : endY;
        float top = mStartY > endY ? endY : mStartY;
        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void onDrawLine(Canvas canvas) {
        canvas.drawLine(mStartX, mStartY, endX, endY, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    boolean isOrientationFixed = false;

    private Matrix matrix = new Matrix();

    private Matrix savedMatrix = new Matrix();
    private Matrix invertMatrix = new Matrix();

    private PointF start = new PointF();
    private PointF mid = new PointF();

    private float[] lastEvent = null;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Log.e("TAG", "onTouchEvent called count " + event.getPointerCount());
        if (event.getPointerCount() == 1) {
            switch (mCurrentShape) {
                case SMOOTHLINE:
                    onTouchEventSmoothLine(event);
                    break;
                case LINE:
                    onTouchEventLine(event);
                    break;
                case RECTANGLE:
                    onTouchEventRectangle(event);
                    break;
                case SQUARE:
                    onTouchEventSquare(event);
                    break;
                case CIRCLE:
                    onTouchEventCircle(event);
                    break;
                case TRIANGLE:
                    onTouchEventTriangle(event);
                    break;
            }
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    lastEvent = null;

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    d = rotation(event);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        float dx = event.getX() - start.x;
                        float dy = event.getY() - start.y;
                        matrix.postTranslate(dx, dy);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (_old_dist == 0) {
                            _old_dist = newDist;
                        }
                        float _distance = newDist - _old_dist;
                        Log.e("TAG", "Difference of the distance newDist " + newDist + " _old_dist " + _old_dist + " diffe " + _distance);
                        _old_dist = newDist;
                        if (event.getPointerCount() == 2 && newDist > 10f) {
                            matrix.set(savedMatrix);

                            scale = (newDist / oldDist);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            PathDrawingActivity._obj_interface.setScale(scale);

                            /*PathDrawingActivity._iv_trace_image.setImageMatrix(matrix);
                            setAnimationMatrix(matrix);

                            invertMatrix = new Matrix(matrix);
                            invertMatrix.invert(invertMatrix);*/
                            Log.e("TAG", "Scale at Zoom " + scale + " newDist " + newDist);
                        }
                        if (lastEvent != null && event.getPointerCount() == 3) {
                            newRot = rotation(event);
                            float r = newRot - d;
                            float[] values = new float[9];
                            matrix.getValues(values);
                            float tx = values[2];
                            float ty = values[5];
                            float sx = values[0];
                            float xc = (getWidth() / 2) * sx;
                            float yc = (getHeight() / 2) * sx;
                            matrix.postRotate(r, tx + xc, ty + yc);
                            PathDrawingActivity._obj_interface.setRotation(r);
                        }
                    }
                    break;
            }
            invalidate();

        }

        if (!isOrientationFixed && event.getAction() == MotionEvent.ACTION_UP) {
            PathDrawingActivity._obj_interface.fixOrientation();
            isOrientationFixed = true;
        }
        return true;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG", "onTouchEvent called count " + event.getPointerCount());

        if (event.getPointerCount() == 1) {
            switch (mCurrentShape) {
                case SMOOTHLINE:
                    onTouchEventSmoothLine(event);
                    break;
                case LINE:
                    onTouchEventLine(event);
                    break;
                case RECTANGLE:
                    onTouchEventRectangle(event);
                    break;
                case SQUARE:
                    onTouchEventSquare(event);
                    break;
                case CIRCLE:
                    onTouchEventCircle(event);
                    break;
                case TRIANGLE:
                    onTouchEventTriangle(event);
                    break;
            }
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    lastEvent = null;

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    d = rotation(event);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        float dx = event.getX() - start.x;
                        float dy = event.getY() - start.y;
                        matrix.postTranslate(dx, dy);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (_old_dist == 0) {
                            _old_dist = newDist;
                        }
                        float _distance = newDist - _old_dist;
                        Log.e("TAG", "Difference of the distance newDist " + newDist + " _old_dist " + _old_dist + " diffe " + _distance);
                        _old_dist = newDist;
                        if (event.getPointerCount() == 2 && newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = (newDist / oldDist);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            PathDrawingActivity._obj_interface.setScale(scale);
                            *//*setScaleX(scale);
                            setScaleY(scale);*//*
                            Log.e("TAG", "Scale at Zoom " + scale + " newDist " + newDist);
                            invalidate();
                        }
                        if (lastEvent != null && event.getPointerCount() == 3) {
                            newRot = rotation(event);
                            float r = newRot - d;
                            float[] values = new float[9];
                            matrix.getValues(values);
                            float tx = values[2];
                            float ty = values[5];
                            float sx = values[0];
                            float xc = (getWidth() / 2) * sx;
                            float yc = (getHeight() / 2) * sx;
                            matrix.postRotate(r, tx + xc, ty + yc);
                            PathDrawingActivity._obj_interface.setRotation(r);
                        }
                    }
                    break;
            }

        }

        if (!isOrientationFixed && event.getAction() == MotionEvent.ACTION_UP) {
            PathDrawingActivity._obj_interface.fixOrientation();
            isOrientationFixed = true;
        }
        return true;
    }*/

    float _old_dist = 0;


    public void onTouchEventSmoothLine(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        Log.e("TAG", "OnTouchSmooth x " + x + " y " + y + " scale " + scale);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                newAdded = true;
                if (!allClear) {
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                    if (bitmap.size() > UndoLimit)
                        bitmap.remove(0);
                    Log.e("TAG", "Bitmap Length OnSmoothLines " + bitmap.size());
                } else allClear = false;
                mPath.reset();
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
    }

    protected float mStartX;
    protected float mStartY;

    protected float endX;
    protected float endY;

    private void onTouchEventLine(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                newAdded = true;
                if (!allClear) {
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                    if (bitmap.size() > UndoLimit)
                        bitmap.remove(0);
                    Log.e("TAG", "Bitmap Length Lines " + bitmap.size());
                } else allClear = false;

                isDrawing = true;
                mStartX = x;
                mStartY = y;
                mPath.reset();
                mPath.moveTo(x, y);
                endX = x;
                endY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = x;
                endY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawLine(mStartX, mStartY, endX, endY, mPaint);
                mPath.lineTo(x, y);
                mCanvas.drawPath(mPath, mPaint);
                mPath = new Path();
                invalidate();
                break;
        }
    }

    public void setBrushSize(int size) {
//        TOUCH_STROKE_WIDTH = size;
        try {
            mPaint.setStrokeWidth(size);
            mBitmapPaint.setStrokeWidth(size);
            currentSize = size;
            if (mPaint.getMaskFilter() != null) {
                mPaint.setMaskFilter(new BlurMaskFilter(currentSize > 1 ? (currentSize / 2) : currentSize, BlurMaskFilter.Blur.NORMAL));
                mBitmapPaint.setMaskFilter(new BlurMaskFilter(currentSize > 1 ? (currentSize / 2) : currentSize, BlurMaskFilter.Blur.NORMAL));
//                Toast.makeText(context, "Mask filter applied", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TAG", "setBrushSize called " + e.getMessage());
        }

    }

    public void setBrushColor(int clr) {
        mPaint.setColor(clr);
        mBitmapPaint.setColor(clr);
    }


    public void undo() {
       /* if (paths.size() > 0) {
            countTouch = 0;
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {
        }*/
        try {

            if (newAdded) {
                bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                newAdded = false;
            }
            if (bitmap.size() > 1) {
                undoBitmap.add(bitmap.remove(bitmap.size() - 1));
                mBitmap = bitmap.get(bitmap.size() - 1).copy(mBitmap.getConfig(), mBitmap.isMutable());
                mCanvas = new Canvas(mBitmap);
                invalidate();
                if (bitmap.size() == 1)
                    allClear = true;
            } else {

            }

            if (mBitmap.sameAs(emptyBitmap)) {
                PathDrawingActivity._obj_interface.releaseOrientation();
            }
        } catch (Exception e) {

        }
    }

    public void redo() {
        try {

            if (undoBitmap.size() > 0) {
                bitmap.add(undoBitmap.remove(undoBitmap.size() - 1));
                mBitmap = bitmap.get(bitmap.size() - 1).copy(mBitmap.getConfig(), mBitmap.isMutable());
                mCanvas = new Canvas(mBitmap);
                invalidate();
            } else {

            }
        } catch (Exception e) {

        }
    }

    public void changeBrush(int type) {
        Log.e("TAG", "Change Brush Called Type " + type);
        try {
            if (type == 0) {
                mPaint.setMaskFilter(null);
                mBitmapPaint.setMaskFilter(null);
            } else {
                mPaint.setMaskFilter(new BlurMaskFilter(currentSize > 1 ? (currentSize / 2) : currentSize, BlurMaskFilter.Blur.NORMAL));
                mBitmapPaint.setMaskFilter(new BlurMaskFilter(currentSize > 1 ? (currentSize / 2) : currentSize, BlurMaskFilter.Blur.NORMAL));
            }
        } catch (Exception e) {
            Log.e("TAG", "changeBrush Exception " + e.getMessage() + " " + e.toString());
        }
    }

    int countTouch = 0;
    float basexTriangle = 0;
    float baseyTriangle = 0;

    public void reset() {
        mPath = new Path();
        countTouch = 0;
    }

    private void onTouchEventRectangle(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                newAdded = true;
                if (!allClear) {
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                    if (bitmap.size() > UndoLimit)
                        bitmap.remove(0);
                    Log.e("TAG", "Bitmap Length Rectangle " + bitmap.size());
                } else allClear = false;
                isDrawing = true;
                mStartX = x;
                mStartY = y;
                endX = x;
                endY = y;
                mPath.reset();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = x;
                endY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                float right = mStartX > endX ? mStartX : endX;
                float left = mStartX > endX ? endX : mStartX;
                float bottom = mStartY > endY ? mStartY : endY;
                float top = mStartY > endY ? endY : mStartY;
                mCanvas.drawRect(left, top, right, bottom, mPaint);
                mPath.addRect(left, top, right, bottom, Path.Direction.CW);
                mCanvas.drawPath(mPath, mPaint);
                mPath = new Path();
                invalidate();
                break;
        }
    }

    private void onTouchEventSquare(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                newAdded = true;
                if (!allClear) {
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                    if (bitmap.size() > UndoLimit)
                        bitmap.remove(0);
                    Log.e("TAG", "Bitmap Length Square " + bitmap.size());
                } else allClear = false;
                isDrawing = true;
                mStartX = x;
                mStartY = y;

                endX = x;
                endY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = x;
                endY = y;
                adjustSquare(endX, endY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                endX = x;
                endY = y;
                adjustSquare(endX, endY);
//                drawRectangle(mCanvas, mPaint);

                Log.e("TAG", "drawRectangle called");
                float right = mStartX > endX ? mStartX : endX;
                float left = mStartX > endX ? endX : mStartX;
                float bottom = mStartY > endY ? mStartY : endY;
                float top = mStartY > endY ? endY : mStartY;
                mCanvas.drawRect(left, top, right, bottom, mPaint);

                mPath.addRect(left, top, right, bottom, Path.Direction.CW);
                mCanvas.drawPath(mPath, mPaint);
                mPath = new Path();
                invalidate();
                break;
        }
    }

    protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mStartX - x);
        float deltaY = Math.abs(mStartY - y);

        float max = Math.max(deltaX, deltaY);

        endX = mStartX - x < 0 ? mStartX + max : mStartX - max;
        endY = mStartY - y < 0 ? mStartY + max : mStartY - max;
    }

    private void onTouchEventCircle(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                newAdded = true;
                if (!allClear) {
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                    if (bitmap.size() > UndoLimit)
                        bitmap.remove(0);
                    Log.e("TAG", "Bitmap Length Circle " + bitmap.size());
                } else allClear = false;
                isDrawing = true;
                mStartX = x;
                mStartY = y;

                endX = x;
                endY = y;

                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                endX = x;
                endY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, endX, endY), mPaint);

//                mPath.lineTo(x, y);
                mPath.addCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, endX, endY), Path.Direction.CCW);
                mCanvas.drawPath(mPath, mPaint);

                mPath = new Path();

                invalidate();
                break;
        }
    }

    /**
     * @return
     */
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }


    private void onDrawCircle(Canvas canvas) {
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, endX, endY), mPaint);
    }

    private void onDrawTriangle(Canvas canvas) {

        if (countTouch < 3) {
            canvas.drawLine(mStartX, mStartY, endX, endY, mPaint);
        } else if (countTouch == 3) {
            canvas.drawLine(endX, endY, mStartX, mStartY, mPaint);
            canvas.drawLine(endX, endY, basexTriangle, baseyTriangle, mPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                newAdded = true;
                if (!allClear) {
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                    if (bitmap.size() > UndoLimit)
                        bitmap.remove(0);
                    Log.e("TAG", "Bitmap Length Triangle " + bitmap.size());
                } else allClear = false;
                countTouch++;
                if (countTouch == 1) {
                    isDrawing = true;
                    mStartX = x;
                    mStartY = y;

                    endX = x;
                    endY = y;

//                    undonePaths.clear();
                    mPath.reset();
                    mPath.moveTo(x, y);
                    mX = x;
                    mY = y;

                } else if (countTouch == 3) {
                    isDrawing = true;
                }

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = x;
                endY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                isDrawing = false;
                endX = x;
                endY = y;
                if (countTouch < 3) {
                    basexTriangle = endX;
                    baseyTriangle = endY;
                    mCanvas.drawLine(mStartX, mStartY, endX, endY, mPaint);

                    mPath.lineTo(x, y);
                    mCanvas.drawPath(mPath, mPaint);
                    mPath = new Path();

                } else if (countTouch >= 3) {
                    mCanvas.drawLine(endX, endY, mStartX, mStartY, mPaint);
                    mCanvas.drawLine(endX, endY, basexTriangle, baseyTriangle, mPaint);


                    mPath.moveTo(endX, endY);
                    mX = x;
                    mY = y;

                    mPath.lineTo(mStartX, mStartY);
                    mCanvas.drawPath(mPath, mPaint);
//                    paths.add(mPath);

                    mPath = new Path();


                    mPath.moveTo(endX, endY);
                    mX = x;
                    mY = y;

                    mPath.lineTo(basexTriangle, baseyTriangle);
                    mCanvas.drawPath(mPath, mPaint);
                    mPath = new Path();
                    countTouch = 0;
                }

                invalidate();
                break;
        }
    }

    public Bitmap getBitmap() {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return mBitmap;
    }

    public void clearPainting() {
        Paint clearPaint = new Paint();
//        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawRect(0, 0, 0, 0, clearPaint);
        mCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
