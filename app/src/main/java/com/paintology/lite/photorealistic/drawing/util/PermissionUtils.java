package com.paintology.lite.photorealistic.drawing.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * <p>
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 19/06/21.
 */
public class PermissionUtils {

    public static boolean checkCameraPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

//    public static boolean checkReadStoragePermission(Context context) {
//        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }

    public static boolean checkStoragePermission(Context context) {

        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
//        }
    }

    public static boolean checkImageReadPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity, int requestCode) {

        if (activity == null) {
            return;
        } else if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // android 13 and above
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // android 11 and above
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
        } else {
            //below android 11
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
        }

    }


        public static void requestStorageAndCameraPermissions(Activity activity, int requestCode) {
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }

            String[] permissions = {
                    Manifest.permission.CAMERA,
                    // Adapt storage permissions to API level
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                            Manifest.permission.READ_MEDIA_IMAGES :
                            Manifest.permission.READ_EXTERNAL_STORAGE
            };

            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }




    public static void requestCameraPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                requestCode);
    }

}
