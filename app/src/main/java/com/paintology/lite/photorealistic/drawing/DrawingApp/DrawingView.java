package com.paintology.lite.photorealistic.drawing.DrawingApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.paintology.lite.photorealistic.drawing.R;

import java.util.ArrayList;

public class DrawingView extends View {

    public static final int LINE = 1;
    public static final int RECTANGLE = 3;
    public static final int SQUARE = 4;
    public static final int CIRCLE = 5;
    public static final int TRIANGLE = 6;
    public static final int SMOOTHLINE = 2;

    public float TOUCH_STROKE_WIDTH = 50;

    public int mCurrentShape;


    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<path_data> paths = new ArrayList<path_data>();
    private ArrayList<path_data> undonePaths = new ArrayList<path_data>();

//    private ArrayList<Path> paths = new ArrayList<Path>();
//    private ArrayList<Path> undonePaths = new ArrayList<Path>();


    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Indicates if you are drawing
     */

    protected boolean isDrawing = false;
    /**
     * Indicates if the drawing is ended
     */
    protected boolean isDrawingEnded = false;


    protected float mStartX;
    protected float mStartY;

    protected float mx;
    protected float my;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
//        for (Path p : paths) {
//            canvas.drawPath(p, mPaint);
//        }

        try {
            CreateBrushActivity._obj_interface.hideLayout();
            for (path_data p : paths) {
                mPaint.setStrokeWidth(p.getStrokeWidth());
                canvas.drawPath(p.get_path(), mPaint);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at onDraw " + e.getMessage() + " " + e.toString());
        }

        Log.e("TAG", "onDraw called stroke width " + TOUCH_STROKE_WIDTH);
        canvas.drawPath(mPath, mPaint);

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
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
//        invalidate();
    }


    protected void init() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.black));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
        mCanvas = new Canvas();
        mPath = new Path();
    }


    public void setBrushColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setBrushSize(int size) {
        TOUCH_STROKE_WIDTH = size;
        mPaint.setStrokeWidth(size);

    }

    public void reset() {
        mPath = new Path();
        countTouch = 0;
    }

    public void clearPainting() {
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawRect(0, 0, 0, 0, clearPaint);
        mCanvas.drawColor(Color.WHITE);
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mx = event.getX();
        my = event.getY();
        switch (mCurrentShape) {
            case LINE:
                onTouchEventLine(event);
                break;
            case SMOOTHLINE:
                onTouchEventSmoothLine(event);
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
        return true;
    }


    //------------------------------------------------------------------
    // Line
    //------------------------------------------------------------------

    private void onDrawLine(Canvas canvas) {

        float dx = Math.abs(mx - mStartX);
        float dy = Math.abs(my - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            canvas.drawLine(mStartX, mStartY, mx, my, mPaint);
        }
    }

   /* private void onTouchEventLine(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                *//*isDrawing = true;
                mStartX = mx;
                mStartY = my;
                mPath.lineTo(mStartX, mStartY);
                undonePaths.clear();
                mPath.reset();
                invalidate();*//*

                undonePaths.clear();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
//                mCanvas.drawLine(mStartX, mStartY, mx, my, mPaint);
//                mPath.lineTo(event.getX(), event.getY());
//                mPath.lineTo(mStartX, mStartY);
//                mPath.rLineTo(mx, my);
//                paths.add(mPath);

                mPath.lineTo(x, y);
                mCanvas.drawPath(mPath, mPaint);
                paths.add(mPath);
                mPath = new Path();

                invalidate();
                break;
        }
    }*/


    private void onTouchEventLine(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
                undonePaths.clear();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawLine(mStartX, mStartY, mx, my, mPaint);
                mPath.lineTo(x, y);
                mCanvas.drawPath(mPath, mPaint);
//                paths.add(mPath);

                path_data _object = new path_data();
                _object.set_path(mPath);
                _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                paths.add(_object);
                mPath = new Path();
                invalidate();
                break;
        }
    }
    //------------------------------------------------------------------
    // Smooth Line
    //------------------------------------------------------------------


    private void onTouchEventSmoothLine(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                /*mStartX = mx;
                mStartY = my;
//                undonePaths.clear();
                mPath.reset();
                mPath.moveTo(mx, my);*/
                Log.e("TAG", "Stroke width at down " + TOUCH_STROKE_WIDTH);

                undonePaths.clear();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

               /* float dx = Math.abs(mx - mStartX);
                float dy = Math.abs(my - mStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mStartX, mStartY, (mx + mStartX) / 2, (my + mStartY) / 2);
                    mStartX = mx;
                    mStartY = my;
                }
                mCanvas.drawPath(mPath, mPaint);
                invalidate();*/

                mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);
                isDrawing = false;

                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
//                paths.add(mPath);
                path_data _object = new path_data();
                _object.set_path(mPath);
                _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                paths.add(_object);
                mPath = new Path();
                invalidate();

                /*mPath.lineTo(mStartX, mStartY);
                mCanvas.drawPath(mPath, mPaint);

                paths.add(mPath);
                mPath = new Path();

//                mPath.reset();
                invalidate();*/
                break;
        }
    }

    //------------------------------------------------------------------
    // Triangle
    //------------------------------------------------------------------

    int countTouch = 0;
    float basexTriangle = 0;
    float baseyTriangle = 0;

    private void onDrawTriangle(Canvas canvas) {

        if (countTouch < 3) {
            canvas.drawLine(mStartX, mStartY, mx, my, mPaint);
        } else if (countTouch == 3) {
            canvas.drawLine(mx, my, mStartX, mStartY, mPaint);
            canvas.drawLine(mx, my, basexTriangle, baseyTriangle, mPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTouch++;
                if (countTouch == 1) {
                    isDrawing = true;
                    mStartX = mx;
                    mStartY = my;

                    undonePaths.clear();
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
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                isDrawing = false;
                if (countTouch < 3) {
                    basexTriangle = mx;
                    baseyTriangle = my;
                    mCanvas.drawLine(mStartX, mStartY, mx, my, mPaint);

                    mPath.lineTo(x, y);
                    mCanvas.drawPath(mPath, mPaint);

                    path_data _object = new path_data();
                    _object.set_path(mPath);
                    _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                    paths.add(_object);
//                    paths.add(mPath);
                    mPath = new Path();

                } else if (countTouch >= 3) {
                    mCanvas.drawLine(mx, my, mStartX, mStartY, mPaint);
                    mCanvas.drawLine(mx, my, basexTriangle, baseyTriangle, mPaint);


                    mPath.moveTo(mx, my);
                    mX = x;
                    mY = y;

                    mPath.lineTo(mStartX, mStartY);
                    mCanvas.drawPath(mPath, mPaint);
//                    paths.add(mPath);
                    path_data _object = new path_data();
                    _object.set_path(mPath);
                    _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);
                    paths.add(_object);
                    mPath = new Path();


                    mPath.moveTo(mx, my);
                    mX = x;
                    mY = y;

                    mPath.lineTo(basexTriangle, baseyTriangle);
                    mCanvas.drawPath(mPath, mPaint);
//                    paths.add(mPath);
                    path_data _object_1 = new path_data();
                    _object_1.set_path(mPath);
                    _object_1.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                    paths.add(_object_1);
                    mPath = new Path();

                    countTouch = 0;
                }

                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas) {
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), mPaint);
    }

    private void onTouchEventCircle(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;

                undonePaths.clear();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), mPaint);

//                mPath.lineTo(x, y);
                mPath.addCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), Path.Direction.CCW);
                mCanvas.drawPath(mPath, mPaint);
//                paths.add(mPath);
                path_data _object = new path_data();
                _object.set_path(mPath);
                _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                paths.add(_object);
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

    //------------------------------------------------------------------
    // Rectangle
    //------------------------------------------------------------------

    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas, mPaint);
    }

    private void onTouchEventRectangle(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;

                undonePaths.clear();
                mPath.reset();
//                mPath.moveTo(x, y);
//                mX = x;
//                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
//                drawRectangle(mCanvas, mPaint);

                float right = mStartX > mx ? mStartX : mx;
                float left = mStartX > mx ? mx : mStartX;
                float bottom = mStartY > my ? mStartY : my;
                float top = mStartY > my ? my : mStartY;
                mCanvas.drawRect(left, top, right, bottom, mPaint);

                mPath.addRect(left, top, right, bottom, Path.Direction.CW);
                mCanvas.drawPath(mPath, mPaint);
//                paths.add(mPath);

                path_data _object = new path_data();
                _object.set_path(mPath);
                _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                paths.add(_object);
                mPath = new Path();
                invalidate();
                break;
        }
        ;
    }

    private void drawRectangle(Canvas canvas, Paint paint) {
        Log.e("TAG", "drawRectangle called");
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;
        canvas.drawRect(left, top, right, bottom, paint);
    }

    //------------------------------------------------------------------
    // Square
    //------------------------------------------------------------------

    private void onDrawSquare(Canvas canvas) {
        onDrawRectangle(canvas);
    }

    private void onTouchEventSquare(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                adjustSquare(mx, my);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                adjustSquare(mx, my);
//                drawRectangle(mCanvas, mPaint);

                Log.e("TAG", "drawRectangle called");
                float right = mStartX > mx ? mStartX : mx;
                float left = mStartX > mx ? mx : mStartX;
                float bottom = mStartY > my ? mStartY : my;
                float top = mStartY > my ? my : mStartY;
                mCanvas.drawRect(left, top, right, bottom, mPaint);

                mPath.addRect(left, top, right, bottom, Path.Direction.CW);
                mCanvas.drawPath(mPath, mPaint);
//                paths.add(mPath);
                path_data _object = new path_data();
                _object.set_path(mPath);
                _object.setStrokeWidth((int) TOUCH_STROKE_WIDTH);

                paths.add(_object);
                mPath = new Path();
                invalidate();
                break;
        }
    }

    /**
     * Adjusts current coordinates to build a square
     *
     * @param x
     * @param y
     */
    protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mStartX - x);
        float deltaY = Math.abs(mStartY - y);

        float max = Math.max(deltaX, deltaY);

        mx = mStartX - x < 0 ? mStartX + max : mStartX - max;
        my = mStartY - y < 0 ? mStartY + max : mStartY - max;
    }


    public Bitmap getBitmap() {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }


    public void undo() {
        if (paths.size() > 0) {
            countTouch = 0;
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {
        }
    }

    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        } else {
        }
    }

    public class path_data {
        public Path _path;
        public int strokeWidth = 0;

        public Path get_path() {
            return _path;
        }

        public void set_path(Path _path) {
            this._path = _path;
        }

        public int getStrokeWidth() {
            return strokeWidth;
        }

        public void setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
        }
    }
}
