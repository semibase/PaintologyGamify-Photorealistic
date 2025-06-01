package com.paintology.lite.photorealistic.drawing.app.manifest;

import android.app.Activity;

import com.paintology.lite.photorealistic.drawing.util.Screen;

public class AppManifest {


    public int mThumbnailWidth = 20;
    public int mThumbnailHeight = 20;

    public int computeBestThumbnailHeight(Activity pActivity) {
        int i = Screen.getScreenHeight(pActivity);
        double d = Screen.getDisplayScaleDensity(pActivity);
        int j = (int) (10.0D * d) * 3;
        int k = (i - j) / 4;
        mThumbnailHeight = k;
        return k;
    }

    public int computeBestThumbnailWidth(Activity pActivity) {
        int i = Screen.getScreenWidth(pActivity);
        double d = Screen.getDisplayScaleDensity(pActivity);
        int j = (int) (10.0D * d) * 3;
        int k = (i - j) / 4;
        mThumbnailWidth = k;
        return k;
    }

    public boolean isProversion() {
        return false;
    }
}
