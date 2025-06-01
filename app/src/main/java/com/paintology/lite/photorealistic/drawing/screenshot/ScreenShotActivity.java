package com.paintology.lite.photorealistic.drawing.screenshot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.MyServiceForRecording;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * <p>
 * Copyright (c) 2022 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 15/09/21.
 */
public class ScreenShotActivity extends AppCompatActivity {

    private static final String TAG = ScreenShotActivity.class.getName();
    public static final int REQUEST_CODE = 550;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mProjection;
    private Intent data_from_result_SS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);

        data_from_result_SS = AppUtils.getDataFromResultSS();
        mediaProjectionManager = AppUtils.getMediaProjectionManager();
        mProjection = AppUtils.getProjection();

        if (mediaProjectionManager == null) {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
//        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);

        try {
            PaintActivity.banner.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        takeScreenshot();

    }

    public void takeScreenshot() {
        try {
            takeSS();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            Log.e(TAG, "Exception at Take SS " + e.toString());
        }
    }

    void takeSS() {
        if (data_from_result_SS == null) {
            if (mediaProjectionManager == null)
                mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);

        } else {
            try {
                PaintActivity.banner.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            final DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            Point size = new Point();
            display.getRealSize(size);
            final int mWidth = size.x;
            final int mHeight = size.y;
            int mDensity = metrics.densityDpi;

            final ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);

            final Handler handler = new Handler();
//            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
//                    | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
//                    | DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION;

            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
            try {
                mProjection = mediaProjectionManager.getMediaProjection(RESULT_OK, data_from_result_SS);
                mProjection.createVirtualDisplay("screen-mirror", mWidth, mHeight, mDensity, flags, mImageReader.getSurface(), null, handler);
            } catch (Exception e) {
                //Recover condition
                mProjection = mediaProjectionManager.getMediaProjection(RESULT_OK, data_from_result_SS);
                mProjection.createVirtualDisplay("screen-mirror", mWidth, mHeight, mDensity, flags, mImageReader.getSurface(), null, handler);
            }

            mImageReader.setOnImageAvailableListener(reader -> {
                reader.setOnImageAvailableListener(null, handler);

                Image image = reader.acquireLatestImage();

                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();

                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * metrics.widthPixels;
                // create bitmap
                Bitmap bmp = Bitmap.createBitmap(metrics.widthPixels + (int) ((float) rowPadding / (float) pixelStride), metrics.heightPixels, Bitmap.Config.ARGB_8888);
                bmp.copyPixelsFromBuffer(buffer);

                image.close();
                reader.close();

                Bitmap realSizeBitmap = Bitmap.createBitmap(bmp, 0, 0, metrics.widthPixels, bmp.getHeight());
                bmp.recycle();

            /*    try {

                    String downloadPath =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                    .toString();
                    File path = new File(downloadPath); //Creates app specific folder

                    if (!path.exists()) {
                        path.mkdirs();
                    }

                    File imageFile = new File(path, System.currentTimeMillis() + ".jpg"); // Imagename.png

                    FileOutputStream out = new FileOutputStream(imageFile);

                    realSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress Image
                    out.flush();
                    out.close();
                    shootSound();
                    Paintor.banner.setVisibility(View.GONE);
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

                    Toast.makeText(ScreenShotActivity.this, R.string.screenshot_saved, Toast.LENGTH_SHORT).show();

                    Uri photoURI = FileProvider.getUriForFile(ScreenShotActivity.this,
                            getString(R.string.authority),
                            imageFile);

                    Intent intent = new Intent(ScreenShotActivity.this, ShareScreenshot.class);
                    intent.putExtra("uri", photoURI);
                    startActivity(intent);

                    finish(); // Close this activity
                } catch (Exception e) {
                    Log.e("TAG", "Exception at generate file " + e.getMessage());
                }*/

                // Replace the existing file generation code with this updated code
                String fileName = System.currentTimeMillis() + ".jpg";
                File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

                Log.e("HH=", "-" + fileName);
                try {
                    FileOutputStream out = new FileOutputStream(imageFile);
                    realSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    shootSound();

                    try {
                        PaintActivity.banner.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Notify the system about the new file
                    MediaScannerConnection.scanFile(
                            this,
                            new String[]{imageFile.getAbsolutePath()},
                            null,
                            (path, uri) -> {
                                // Handle the scanned file URI here, if needed
                            }
                    );

                    Toast.makeText(ScreenShotActivity.this, R.string.screenshot_saved, Toast.LENGTH_SHORT).show();

                    Uri photoURI = FileProvider.getUriForFile(
                            ScreenShotActivity.this,
                            getString(R.string.authority),
                            imageFile
                    );

                    Intent intent = new Intent(ScreenShotActivity.this, ShareScreenshot.class);
                    intent.putExtra("uri", photoURI);
                    startActivity(intent);
                    finish(); // Close this activity
                } catch (Exception e) {
                    Log.e(TAG, "Exception at generate file " + e.getMessage());
                }

            }, handler);
        }
    }

    public void shootSound() {
        try {
            MediaActionSound sound = new MediaActionSound();
            sound.play(MediaActionSound.SHUTTER_CLICK);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            mProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            AppUtils.setProjection(mProjection);
            data_from_result_SS = data;
            AppUtils.setDataFromResultSS((Intent) data_from_result_SS.clone());
            takeScreenshot();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(ScreenShotActivity.this, MyServiceForRecording.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            PaintActivity.banner.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}