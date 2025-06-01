package com.paintology.lite.photorealistic.drawing.photo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Gallery {

    public static Bitmap cropImage(Bitmap pBitmap, int pInt1, int pInt2) {
        Rect lRect1 = new Rect();
        int i = pBitmap.getWidth();
        int j = pBitmap.getHeight();

        int i1 = 0;

        if (i < j) {
            i1 = 1;
            int i2 = j;
            j = i;
            i = i2;
        }
        int i3 = pInt1 * j;
        int i4 = i * pInt2;
        int i5;
        int i9;
        if (i3 > i4) {
            i5 = i;
            int i6 = i5 * pInt2 / pInt1;
            lRect1.left = 0;
            int i7 = i5;
            lRect1.right = i7;
            int i8 = (j - i6) / 2;
            lRect1.top = i8;
            i9 = lRect1.top + i6;
            lRect1.bottom = i9;
        } else {
            int i22;
            i22 = j;
            i5 = pInt1 * i22 / pInt2;
            int i23 = (i - i5) / 2;
            lRect1.left = i23;
            int i24 = lRect1.left + i5;
            lRect1.right = i24;
            lRect1.top = 0;
            lRect1.bottom = i22;
        }
        {
            int i10 = pInt1;
            int i11 = pInt2;
            Rect lRect2 = new Rect(0, 0, i10, i11);
            Matrix lMatrix = new Matrix();
            float f1 = pInt1 * 1.0F;
            float f2 = i5;
            float f3 = f1 / f2;
            float f4 = f3;
            float f5 = f3;
            boolean bool1 = lMatrix.postScale(f4, f5);
            if (i1 != 0) {
                Rect lRect3 = new Rect();
                Rect lRect4 = lRect3;
                Rect lRect5 = lRect1;
                lRect4.set(lRect5);
                int i12 = lRect3.top;
                lRect1.left = i12;
                int i13 = lRect3.bottom;
                lRect1.right = i13;
                int i14 = lRect3.left;
                lRect1.top = i14;
                int i15 = lRect3.right;
                lRect1.bottom = i15;
                boolean bool2 = lMatrix.postRotate(90.0F);
            }
            int i16 = lRect1.left;
            int i17 = lRect1.top;
            int i18 = lRect1.width();
            int i19 = lRect1.height();
            Bitmap lBitmap1 = Bitmap.createBitmap(pBitmap, i16, i17, i18, i19, lMatrix, true);
            Bitmap.Config lConfig = Bitmap.Config.RGB_565;
            int i20 = pInt1;
            int i21 = pInt2;
            Bitmap lBitmap2 = Bitmap.createBitmap(i20, i21, lConfig);
            Canvas lCanvas = new Canvas(lBitmap2);
            Bitmap lBitmap3 = lBitmap1;
            if (lBitmap3 != null && !lBitmap3.isRecycled())
                lCanvas.drawBitmap(lBitmap3, 0.0F, 0.0F, null);
            lBitmap1.recycle();
            return lBitmap2;
        }
    }

    public static Bitmap getGalleryCropScaledPhoto(String pString, int pInt1, int pInt2) {
        Bitmap lBitmap1 = scaleDecodeFile(pString, pInt1, pInt2);
        Bitmap lBitmap2;
        if (lBitmap1 != null && !lBitmap1.isRecycled()) {
            lBitmap2 = cropImage(lBitmap1, pInt1, pInt2);
            lBitmap1.recycle();
        } else
            return null;

        return lBitmap2;
    }

    public static Bitmap getImage(Activity pActivity, Intent pIntent, int pInt1, int pInt2) {
        Uri lUri = pIntent.getData();
        String[] arrayOfString1 = new String[1];
        arrayOfString1[0] = "_data";
        ContentResolver lContentResolver = pActivity.getContentResolver();
        String[] arrayOfString2 = null;
        String str1 = null;
        Cursor lCursor = lContentResolver.query(lUri, arrayOfString1, null, arrayOfString2, str1);
        boolean bool = lCursor.moveToFirst();
        String str2 = arrayOfString1[0];
        int i = lCursor.getColumnIndex(str2);
        String str3 = lCursor.getString(i);
        lCursor.close();
        return scaleDecodeFile(str3, pInt1, pInt2);
    }

    public static String getImagePath(Activity pActivity, Intent pIntent) {
        Uri lUri = pIntent.getData();
        String[] arrayOfString1 = new String[1];
        arrayOfString1[0] = "_data";
        ContentResolver lContentResolver = pActivity.getContentResolver();
        String[] arrayOfString2 = null;
        String str1 = null;
        Cursor lCursor = lContentResolver.query(lUri, arrayOfString1, null, arrayOfString2, str1);
        boolean bool = lCursor.moveToFirst();
        String str2 = arrayOfString1[0];
        int i = lCursor.getColumnIndex(str2);
        String str3 = lCursor.getString(i);
        lCursor.close();
        return str3;
    }

    static Bitmap getScaleImage(String pString, int pInt1, int pInt2) {
        return scaleDecodeFile(pString, pInt1, pInt2);
    }

    private static Bitmap scaleDecodeFile(String pString, int pInt1, int pInt2) {
        try {
            BitmapFactory.Options lOptions1 = new BitmapFactory.Options();
            lOptions1.inJustDecodeBounds = true;
            File lFile = new File(pString);
            BitmapFactory.decodeStream(new FileInputStream(lFile), null, lOptions1);
            int k = lOptions1.outWidth;
            int m = lOptions1.outHeight;


            if (((pInt1 > pInt2) && (k < m)) || ((pInt1 < pInt2) && (k > m))) {
                int n = m;
                m = k;
                k = n;
            }
            int i1 = 1;
            while ((k >= pInt1) || (m >= pInt2)) {
                k /= 2;
                m /= 2;
                i1 += 1;
            }
            BitmapFactory.Options lOptions2 = new BitmapFactory.Options();
            lOptions2.inSampleSize = i1;
            lOptions2.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap lBitmap2 = BitmapFactory.decodeStream(new FileInputStream(lFile), null, lOptions2);
            return lBitmap2;
        } catch (FileNotFoundException lFileNotFoundException) {
            return null;
        }
    }

    public static void startGallery(Activity pActivity, int pInt) {
        Intent lIntent1 = new Intent();
        Intent lIntent2 = lIntent1.setType("image/*");
        Intent lIntent3 = lIntent1.setAction("android.intent.action.GET_CONTENT");
        Intent lIntent4 = Intent.createChooser(lIntent1, "Select Picture");
        pActivity.startActivityForResult(lIntent4, pInt);
    }
}
