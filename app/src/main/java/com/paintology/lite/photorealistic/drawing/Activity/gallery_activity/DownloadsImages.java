package com.paintology.lite.photorealistic.drawing.Activity.gallery_activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.paintology.lite.photorealistic.drawing.util.KGlobal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadsImages extends AsyncTask<String, Void, Void> {

    private Context context;
    private String fileName;
    private Boolean TraceImage;

    public DownloadsImages(Context context, String fileName, Boolean TraceImage) {
        this.context = context;
        if (fileName == null) {
            // handle null fileName appropriately, maybe assign a default value or throw an exception
            Log.e("DownloadsImages", "File name cannot be null");
            // Example: throw new IllegalArgumentException("File name cannot be null");
        } else {
            this.fileName = fileName;
        }
        this.TraceImage = TraceImage;
    }

    @Override
    protected Void doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Check if URL is null
        if (url != null) {
            File path;

            // Check condition and assign path accordingly
            if (TraceImage) {
                path = new File(KGlobal.getTraceImageFolderPath(context));
            } else {
                path = new File(KGlobal.getTraceImageFolderPathForSpecificUser(context));
            }

            if (!path.exists()) {
                path.mkdirs();
            }

            // Extract file name from the URL
            File imageFile = new File(path, fileName);

            // Check if the file already exists
            if (!imageFile.exists()) {
                try {
                    Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    FileOutputStream out = new FileOutputStream(imageFile);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {}
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getFileNameFromUrl(String url) {
        // Extract the file name from the URL
        return url.substring(url.lastIndexOf('/') + 1);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("TAG", "onPostExecute: Image Saved!");
    }
}