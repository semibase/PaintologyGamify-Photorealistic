package com.paintology.lite.photorealistic.drawing.DashboardScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingViewTest extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint, drawPaint;
    private Path circlePath;
    Boolean eraserOn = false;
    Boolean newAdded = false;
    Boolean allClear = false;
    private Path drawPath;

    private ArrayList<Bitmap> bitmap = new ArrayList<>();
    private ArrayList<Bitmap> undoBitmap = new ArrayList<>();

    public DrawingViewTest(Context context) {
        super(context);
        setupDrawing();

    }


    private void setupDrawing() {
//get drawing area setup for interaction
        drawPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setColor(Color.BLACK);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // if(!eraserOn)
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(drawPath, drawPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    public void onClickEraser(boolean isEraserOn) {
        if (isEraserOn) {
            eraserOn = true;
            drawPaint.setColor(getResources().getColor(android.R.color.transparent));
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        } else {
            eraserOn = false;
            drawPaint.setColor(mBitmapPaint.getColor());
            drawPaint.setXfermode(null);
        }
    }


    public void onClickUndo() {
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
        //toast the user
    }

    public void onClickRedo() {
        if (undoBitmap.size() > 0) {
            bitmap.add(undoBitmap.remove(undoBitmap.size() - 1));
            mBitmap = bitmap.get(bitmap.size() - 1).copy(mBitmap.getConfig(), mBitmap.isMutable());
            mCanvas = new Canvas(mBitmap);
            invalidate();
        } else {

        }
        //toast the user
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                newAdded = true;
                if (!allClear)
                    bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
                else allClear = false;

                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (eraserOn) {
                    drawPath.lineTo(touchX, touchY);
                    mCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    drawPath.moveTo(touchX, touchY);
                } else {
                    drawPath.lineTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            case MotionEvent.ACTION_CANCEL:
                return false;

            default:
                return false;
        }

        invalidate();
        return true;

    }
}
