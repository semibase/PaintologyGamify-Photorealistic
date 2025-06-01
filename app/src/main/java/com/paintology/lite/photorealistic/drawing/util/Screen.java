package com.paintology.lite.photorealistic.drawing.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Screen {


    public static float getDisplayScaleDensity(Activity pActivity) {
        DisplayMetrics lDisplayMetrics = new DisplayMetrics();
        pActivity.getWindowManager().getDefaultDisplay().getMetrics(lDisplayMetrics);
        return lDisplayMetrics.scaledDensity;
    }

    public static int getScreenHeight(Context pContext) {
        DisplayMetrics lDisplayMetrics1 = new DisplayMetrics();
        DisplayMetrics lDisplayMetrics2 = pContext.getApplicationContext().getResources().getDisplayMetrics();
        int i = lDisplayMetrics2.widthPixels;
        return lDisplayMetrics2.heightPixels;
    }

    public static int getScreenWidth(Context pContext) {
        DisplayMetrics lDisplayMetrics1 = new DisplayMetrics();
        DisplayMetrics lDisplayMetrics2 = pContext.getApplicationContext().getResources().getDisplayMetrics();
        int i = lDisplayMetrics2.widthPixels;
        int j = lDisplayMetrics2.heightPixels;
        return i;
    }
}
