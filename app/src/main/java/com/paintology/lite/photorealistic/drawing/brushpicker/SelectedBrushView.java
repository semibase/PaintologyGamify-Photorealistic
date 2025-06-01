package com.paintology.lite.photorealistic.drawing.brushpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.paintology.lite.photorealistic.drawing.brush.Brush;
import com.paintology.lite.photorealistic.drawing.painting.Painting;
import com.paintology.lite.photorealistic.drawing.painting.Point;

public class SelectedBrushView extends View {
    private String TAG = "SelectedBrushView";
    private Brush mBrush;
    private int mHeight;
    Painting mPainting;
    private Path mPath;
    private float[] mPoints;
    private int mWidth;
    private float mYdensity;

    public SelectedBrushView(Context pContext, AttributeSet pAttributeSet) {
        super(pContext, pAttributeSet);
        Path lPath = new Path();
        mPath = lPath;
        String str = getDisplayDensity(pContext);
        Painting lPainting = new Painting(pContext);
        mPainting = lPainting;
    }

    public SelectedBrushView(Context pContext, AttributeSet pAttributeSet, int pInt) {
        super(pContext, pAttributeSet, pInt);
        Path lPath = new Path();
        mPath = lPath;
        String str = getDisplayDensity(pContext);
        Painting lPainting = new Painting(pContext);
        mPainting = lPainting;
    }

    protected void MyDbgLog(String pString1, String pString2) {
    }

    protected void drawEraserBrush(Canvas pCanvas) {
        float f1 = mYdensity;
        float f2 = 70.0F * f1;
        float f3 = f2 / 2.0F;
        float f4 = f3;
        float f5 = f3;
        float f6 = pCanvas.getWidth() - 20.0F;
        float f7 = pCanvas.getWidth() / 2;
        float f8 = (20.0F + f7) / 2.0F;
        float f9 = (f7 + f6) / 2.0F;
        float f10 = f5 / 2.0F;
        float f11 = (f5 + f2) / 2.0F;
        mPath.reset();
        mPath.moveTo(20.0F, f5);
        mPath.quadTo(f8, f10, f7, f4);
        mPath.quadTo(f9, f11, f6, f3);
        pCanvas.drawColor(-1);
        if ((mBrush != null) && (mBrush.mBrushStyle != 39)) {
            if (mBrush.getRandomColorPicker() != null)
                mBrush.getRandomColorPicker().resetPicker();
            mBrush.setAlpha(255);
            mBrush.prepareBrush();
            if (mBrush.mBrushStyle < 512)
                mBrush.drawStroke(pCanvas, mPath);
            else {
                Brush lBrush1 = mBrush;
                Point lPoint1 = new Point(20.0F, f5);
                Point lPoint2 = new Point(f8, f10);
                Point lPoint3 = new Point(f7, f4);
                Canvas lCanvas1 = pCanvas;
                Rect lRect1 = lBrush1.drawStroke(lCanvas1, lPoint1, lPoint2, lPoint3);
                Brush lBrush2 = mBrush;
                Point lPoint4 = new Point(f7, f4);
                Point lPoint5 = new Point(f9, f11);
                Point lPoint6 = new Point(f6, f3);
                Canvas lCanvas2 = pCanvas;
                Rect lRect2 = lBrush2.drawStroke(lCanvas2, lPoint4, lPoint5, lPoint6);
            }
        }
        mBrush.endStroke();
    }

    protected void finish() {
        mPainting.deinit();
        mPainting = null;
        mBrush = null;
        mPath = null;
    }

    public String getDisplayDensity(Context pContext) {
        DisplayMetrics lDisplayMetrics1 = new DisplayMetrics();
        DisplayMetrics lDisplayMetrics2 = pContext.getApplicationContext().getResources().getDisplayMetrics();
        int i = lDisplayMetrics2.widthPixels;
        int j = lDisplayMetrics2.heightPixels;
        float f1 = lDisplayMetrics2.density;
        float f2 = lDisplayMetrics2.xdpi;
        float f3 = lDisplayMetrics2.ydpi;
        mYdensity = f1;
        String str1 = String.valueOf("");
        StringBuilder lStringBuilder1 = new StringBuilder(str1).append("The absolute width:");
        String str2 = String.valueOf(i);
        String str3 = String.valueOf(str2 + "pixels\n");
        StringBuilder lStringBuilder2 = new StringBuilder(str3).append("The absolute heightin:");
        String str4 = String.valueOf(j);
        String str5 = String.valueOf(str4 + "pixels\n");
        StringBuilder lStringBuilder3 = new StringBuilder(str5).append("The logical density of the display.:");
        String str6 = String.valueOf(f1);
        String str7 = String.valueOf(str6 + "\n");
        StringBuilder lStringBuilder4 = new StringBuilder(str7).append("X dimension :");
        String str8 = String.valueOf(f2);
        String str9 = String.valueOf(str8 + "pixels per inch\n");
        StringBuilder lStringBuilder5 = new StringBuilder(str9).append("Y dimension :");
        String str10 = String.valueOf(f3);
        return str10 + "pixels per inch\n";
    }

    protected void onDraw(Canvas pCanvas) {
        try {
            if (mBrush.mBrushStyle == 112) {
                drawEraserBrush(pCanvas);
                return;
            }
            Log.e("TAG", "onDraw at selectedBrush " + mBrush.mBrushStyle);
            if (mBrush.mBrushStyle >= 256 && mBrush.mBrushStyle <= 511) {
                mPainting.clearPainting();
                mPainting.mBrushDemoMode = true;

                if (mBrush.mBrushStyle != 266 && mBrush.mBrushStyle != 267) {
                    mPainting.strokeFrom(mPoints[0], mPoints[1]);

                    for (int i11 = 1; i11 < mPoints.length / 2; i11++) {
                        mPainting.strokeTo(mPoints[i11 * 2], mPoints[i11 * 2 + 1]);
                    }
                }
                mPainting.strokeEnd(mPoints[0], mPoints[1]);
                if (mPainting.getPainting() != null && !mPainting.getPainting().isRecycled())
                    pCanvas.drawBitmap(mPainting.getPainting(), 0.0F, 0.0F, null);
            }

            mPainting.clearPainting();
            float f6 = (mWidth - 50.0F - 50.0F) / 16.0F;
            float f8 = 0.0F;
            float f1 = 50.0F;
            float f9 = mHeight / 2;
            float f10 = mHeight / 4;
            float f2 = (float) Math.sin(f8) * f10 + f9;
            mPainting.strokeFrom(f1, f2);

            for (int i19 = 0; i19 < 16.0F; i19++) {
                f1 += f6;
                f8 += 6.283186F / 16.0F;
                f2 = (float) Math.sin(f8) * f10 + f9;
                mPainting.strokeTo(f1, f2);
            }

            mPainting.strokeEnd(f1, f2);
            if (mPainting.getPainting() != null && !mPainting.getPainting().isRecycled())
                pCanvas.drawBitmap(mPainting.getPainting(), 0.0F, 0.0F, null);

        } catch (Exception e) {
            Log.e("TAG", "Exception at onDraw  " + e.getMessage());
        }
    }

    protected void onSizeChanged(int pInt1, int pInt2, int pInt3, int pInt4) {
        String str = "onSizeChanged" + pInt1 + ", " + pInt2 + ", " + pInt3 + "," + pInt4;
        int i = Log.i("brush view", str);
        if ((pInt1 <= 0) || (pInt2 <= 0))
            return;

        mWidth = pInt1;
        mHeight = pInt2;
        mPainting.createCanvas(pInt1, pInt2);
    }

    public void setBrush(Brush pBrush) {
        mBrush = pBrush;
        Painting lPainting1 = mPainting;
        int i = pBrush.mBrushStyle;
        lPainting1.setBrushStyle(i);
        Painting lPainting2 = mPainting;
        int j = pBrush.mBrushColor;
        lPainting2.setBrushColor(j);
        Painting lPainting3 = mPainting;
        int k = pBrush.mBrushAlphaValue;
//        int k = 500;
        lPainting3.mBrushAlpha = k;
        Painting lPainting4 = mPainting;
        float f = pBrush.mBrushSize;
        lPainting4.setBrushSize(f);
        mPainting.setBackgroundColor(-1);
        Painting lPainting5 = mPainting;
        int m = pBrush.mBrushFlow;
        lPainting5.mBrushFlow = m;
        mPainting.mBrushKidOrArtistMode = 33;
        Log.e("TAG", "BrushAlpha Value at SelectedBrush " + lPainting3.mBrushAlpha);
    }

    public void setStrokePoints(float[] pArrayOfFloat) {
        mPoints = pArrayOfFloat;
    }
}
