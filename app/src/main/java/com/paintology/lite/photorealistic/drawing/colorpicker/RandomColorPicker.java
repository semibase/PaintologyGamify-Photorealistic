package com.paintology.lite.photorealistic.drawing.colorpicker;

import android.graphics.Color;

import java.util.Random;

public class RandomColorPicker {
    private String TAG = "RandomColorPicker";
    private ColorPref mColorPreference;
    private int mColumnColorCount;
    int mCurColorIndex;
    float[] mHSV;
    private HSV[] mHsvTable;
    int mNumColors;
    Random mRandom;
    private int mRandomColorColumn;

    public RandomColorPicker(int pInt, ColorPref pColorPref) {
        float[] arrayOfFloat = new float[3];
        mHSV = arrayOfFloat;
        mNumColors = pInt;
        mColorPreference = pColorPref;
        Random lRandom1 = new Random();
        mRandom = lRandom1;
        Random lRandom2 = mRandom;
        long l = System.currentTimeMillis();
        lRandom2.setSeed(l);
        mCurColorIndex = 0;
        mRandomColorColumn = 0;
        int i = pInt / 6;
        mColumnColorCount = i;
        createColorTable();
    }

    private void MyDbgLog(String pString1, String pString2) {
    }

    public int convertToColor(HSV pHSV) {
        float[] arrayOfFloat1 = mHSV;
        float f1 = pHSV.h;
        arrayOfFloat1[0] = f1;
        float[] arrayOfFloat2 = mHSV;
        float f2 = pHSV.s;
        arrayOfFloat2[1] = f2;
        float[] arrayOfFloat3 = mHSV;
        float f3 = pHSV.v;
        arrayOfFloat3[2] = f3;
        return Color.HSVToColor(mHSV);
    }

    public void createColorTable() {
        if (mColorPreference == ColorPref.BRIGHT_COLOR) {
            mHsvTable = new HSV[mNumColors];

            for (int i = 0; i < mNumColors; i++) {
                HSV lHSV1 = new HSV();
                lHSV1.s = 1.0F;
                lHSV1.v = 1.0F;
                float f1 = mNumColors;
                float f2 = 360.0F / f1;
                float f3 = i;
                float f4 = f2 * f3;
                lHSV1.h = f4;
                mHsvTable[i] = lHSV1;
            }
        } else {
            mHsvTable = new HSV[mNumColors];

            for (int i = 0; i < mNumColors; i++) {
                HSV lHSV2 = new HSV();
                lHSV2.s = 1.0F;
                lHSV2.v = 0.92F;
                float f5 = mNumColors;
                float f6 = 360.0F / f5;
                float f7 = i;
                float f8 = f6 * f7;
                lHSV2.h = f8;
                mHsvTable[i] = lHSV2;
            }
        }
    }

    public ColorPref getColorPreference() {
        return mColorPreference;
    }

    public int getNextColor() {
        int i = mCurColorIndex + 1;
        mCurColorIndex = i;
        int j = mCurColorIndex;
        int k = mNumColors;
        if (j >= k)
            mCurColorIndex = 0;
        HSV[] arrayOfHSV = mHsvTable;
        int m = mCurColorIndex;
        HSV lHSV = arrayOfHSV[m];
        return convertToColor(lHSV);
    }

    public int getRandomColor() {
        HSV lHSV1;
        HSV lHSV2;
        int n;

        if (1 == 0) {
            Random lRandom1 = mRandom;
            int i = mNumColors;
            int j = lRandom1.nextInt(i);
            mCurColorIndex = j;
            MyDbgLog(TAG, "index " + mCurColorIndex);
            lHSV1 = mHsvTable[mCurColorIndex];
            n = convertToColor(lHSV1);
        } else {
            int i1 = mRandomColorColumn;
            int i2 = mColumnColorCount;
            int i3 = i1 * i2;
            Random lRandom2 = mRandom;
            int i4 = mColumnColorCount;
            int i5 = lRandom2.nextInt(i4);
            int i6 = i3 + i5;
            mCurColorIndex = i6;
            int i7 = mNumColors + -1;
            int i8 = mCurColorIndex;
            int i9 = Math.min(i7, i8);
            mCurColorIndex = i9;
            int i11 = mRandomColorColumn + 1;
            mRandomColorColumn = i11;
            if (mRandomColorColumn > 5)
                mRandomColorColumn = 0;
            HSV[] arrayOfHSV2 = mHsvTable;
            int i12 = mCurColorIndex;
            lHSV2 = arrayOfHSV2[i12];
            n = convertToColor(lHSV2);
        }

        return n;
    }

    public void resetPicker() {
        mCurColorIndex = 0;
    }

    public void setBrightDarkPreference(ColorPref pColorPref) {
        mColorPreference = pColorPref;
    }

    public enum ColorPref {
        BRIGHT_COLOR,
        DARK_COLOR,
    }

    class HSV {
        public float h;
        public float s;
        public float v;

        public HSV() {
        }

        public HSV(float pFloat1, float pFloat2, float arg4) {
            h = pFloat1;
            s = pFloat2;
            v = arg4;
        }
    }
}
