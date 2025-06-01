package com.paintology.lite.photorealistic.drawing.brush;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.brushpicker.BrushPickerActivity;
import com.paintology.lite.photorealistic.drawing.painting.Point;

import java.util.Random;

public class InkPenBrushNew extends Brush {
    private String TAG;
    private Random mRandom;


    public InkPenBrushNew() {
        Random lRandom = new Random();
        mRandom = lRandom;
        TAG = "LineBrush";
        int i = mRandom.nextInt(56);
        mBrushGreyValue = i;
        /*mBrushMaxSize = 25.0F;
        mBrushMinSize = 1.0F;
        mBrushSize = 5.0F;
        mSizeBias = 1.5F;
        */


        mBrushMaxSize = 35.0F;
        mBrushMinSize = 0.01F;
        mBrushSize = 5.0F;
        mSizeBias = 1.5F;

        mBrushHasAlpha = true;
        mHasGlobalAlpha = true;
        mBrushStyle = 56;
        mBrushMode = 17;
        mMustRedrawWholeStrokePath = false;
        mIsRandomColor = true;
        mBrushArchiveDataSize = 3;

    }

    private int randomAlpha(int pInt1, int pInt2, Random pRandom) {
        int i = pInt2 * 2 + 1;
        int j = pRandom.nextInt(i) - pInt2;
        pInt1 += j;
        if (pInt1 <= 100)
            pInt1 = 100;
        if (pInt1 > 220)
            pInt1 = 220;
        return pInt1;
    }

    private int randomAlpha(int pInt, Random pRandom) {
        int i = pRandom.nextInt(21) + -10;
        pInt += i;
        if (pInt <= 100)
            pInt = 100;
        if (pInt > 250)
            pInt = 250;
        return pInt;
    }

    private float randomWidth(float pFloat, int pInt, Random pRandom) {
        int i = pInt * 2 + 1;
        float f1 = pRandom.nextInt(i) - pInt;
        pFloat += f1;
        float f2 = mBrushMinSize;
        if (pFloat <= f2)
            pFloat = mBrushMaxSize / 2.0F;
        float f3 = mBrushMaxSize;
        if (pFloat > f3)
            pFloat = mBrushMaxSize;
        return pFloat;
    }

    private float randomWidth(float pFloat, Random pRandom) {
        float f1 = (pRandom.nextInt(5) + -2) * 0.6F;
        float f2 = pFloat + f1;
        float f3 = mSizeLowerBound;
        float f4 = Math.max(f2, f3);
        float f5 = mSizeUpperBound;
        return Math.min(f4, f5);
    }

    public float[] archiveBrush() {
        float[] arrayOfFloat = new float[3];
        float f1 = mBrushSize;
        arrayOfFloat[0] = f1;
        float f2 = mBrushAlphaValue;
        arrayOfFloat[1] = f2;
        float f3 = mBrushGreyValue;
        arrayOfFloat[2] = f3;
        return arrayOfFloat;
    }

    public int[] archivePaint() {
        return null;
    }

    public void draw(Canvas pCanvas, Path pPath) {
    }

    public void draw(Canvas pCanvas, Path pPath, int pInt) {
    }

    public void draw(Canvas pCanvas, Point[] pArrayOfPoint) {
    }

    public void draw(Canvas pCanvas, Point[] pArrayOfPoint, int pInt) {
    }

    public void endBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
        drawLine(pCanvas, pPoint1, pPoint2);
    }

    public void prepareBrush() {
        if ((mBrushMode == 17) && (mRandomColorPicker != null)) {
            int i = mRandomColorPicker.getRandomColor();
            mBrushColor = i;
        }
        preparePaint();
    }

    Paint lPaint5;

    public void preparePaint() {
        mBrushPaint.setAntiAlias(true);


        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
       /* Paint lPaint1 = mBrushPaint;
        Paint.Cap lCap = Paint.Cap.ROUND;
        lPaint1.setStrokeCap(lCap);
        Paint lPaint2 = mBrushPaint;
        Paint.Join lJoin = Paint.Join.ROUND;
        lPaint2.setStrokeJoin(lJoin);
        Paint lPaint3 = mBrushPaint;
        Paint.Style lStyle = Paint.Style.STROKE;
        lPaint3.setStyle(lStyle);
        Paint lPaint4 = mBrushPaint;
        float f = mBrushSize;
        lPaint4.setStrokeWidth(f);
        */

        lPaint5 = mBrushPaint;
        int i = mBrushColor;


        lPaint5.setAntiAlias(true);
        lPaint5.setStrokeCap(Paint.Cap.ROUND);
        lPaint5.setStyle(Paint.Style.STROKE);
        lPaint5.setStrokeWidth(mBrushSize);
//        lPaint5.setStrokeWidth(0.2f);
        lPaint5.setStrokeJoin(Paint.Join.ROUND);


//        Typeface plain = Typeface.createFromAsset(MyApplication.getInstance().getAssets(), "custom_font.ttf");
//        Typeface bold = Typeface.create(plain, 0, true);
//        Paint paint = new Paint();
//        Typeface bold = ResourcesCompat.getFont(MyApplication.getInstance(), R.font.custom_font);
//        lPaint5.setTypeface(bold);
//

       /* lPaint5.setPathEffect(new ComposePathEffect(
                new DashPathEffect(new float[]{mBrushSize * 0.8f, mBrushSize * 0.2f}, 0),
                new CornerPathEffect(mBrushSize * 0.2f)));*/

        /*float[] mColors = new float[]{Color.BLACK, Color.RED, Color.BLUE,
                Color.GREEN, Color.MAGENTA, Color.BLACK
        };*/

        lPaint5.setColor(i);

    }

    public void randomPaint(Paint pPaint, Random pRandom) {
        preparePaint();
    }

    public void restoreBrush(float[] pArrayOfFloat) {
//        Log.e(TAG, "restoreBrush called");
        mBrushSize = pArrayOfFloat[0];
        mBrushAlphaValue = (int) pArrayOfFloat[1];
        mBrushGreyValue = (int) pArrayOfFloat[2];
        restorePaint();
    }

    public void restorePaint() {
        preparePaint();
    }

    public void restorePaint(int[] pArrayOfInt) {
    }

    public void startBrush(Canvas pCanvas, Point pPoint1, Point pPoint2) {
        drawLine(pCanvas, pPoint1, pPoint2);
    }

    public void updateBrush() {
        mBrushSize = (float) getsize();
        Log.e("TAG", "Update Brush called Line " + mBrushSize + " " + BrushPickerActivity.getmBrushSize());
        preparePaint();
    }

    float randomSize = BrushPickerActivity.getmBrushSize();

    boolean isReachToEdge = true;
    float maxSize = BrushPickerActivity.getmBrushSize();

    //    maxSize = BrushPickerActivity.getmBrushSize();
    public double getsize() {
        try {

            Log.e("TAG", "Random Size Before " + randomSize + " slop " + getSlop() + " maxSize " + maxSize);
            if (randomSize >= maxSize) {
                isReachToEdge = true;
//                randomSize += getSlop();
            } else if (randomSize <= 1) {
                isReachToEdge = false;
//                randomSize -= getSlop();
            }
            if (!isReachToEdge) {
                randomSize += getSlop();
            } else
                randomSize -= getSlop();
            Log.e("TAG", "Random Size After " + randomSize + " slop " + getSlop() + " maxSize " + maxSize);
        } catch (Exception e) {

        }
        return randomSize;
    }


    public float getSlop() {
        try {
            if (BrushPickerActivity.getmBrushSize() <= 10) {
                return 0.5f;
            } else if (BrushPickerActivity.getmBrushSize() >= 10 && BrushPickerActivity.getmBrushSize() <= 15) {
                return 0.6f;
            } else if (BrushPickerActivity.getmBrushSize() >= 15 && BrushPickerActivity.getmBrushSize() <= 20) {
                return 0.7f;
            } else
                return 0.7f;
        } catch (Exception e) {

        }
        return 1;
    }
}
